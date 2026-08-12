package th.mfu.pvz.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import feign.FeignException;
import th.mfu.pvz.order.client.CatalogClient;
import th.mfu.pvz.order.client.CustomerClient;
import th.mfu.pvz.order.domain.Order;
import th.mfu.pvz.order.domain.OrderItem;
import th.mfu.pvz.order.dto.CustomerDTO;
import th.mfu.pvz.order.dto.OrderDTO;
import th.mfu.pvz.order.dto.OrderItemDTO;
import th.mfu.pvz.order.dto.ProductDTO;
import th.mfu.pvz.order.dto.mapper.OrderMapper;
import th.mfu.pvz.order.event.OrderEventPublisher;
import th.mfu.pvz.order.event.OrderPlacedEvent;
import th.mfu.pvz.order.repository.OrderRepository;

/**
 * REST API for orders - the centre of the system.
 *
 * Every method follows the same three steps:
 *   1. talk to the repository, in ENTITIES
 *   2. use the mapper to convert
 *   3. answer the client, in DTOs
 *
 * createOrder does two extra things no other controller in the project does:
 * it CALLS two services over Feign, and it PUBLISHES an event to Kafka.
 */
@RestController
@RequestMapping("/api")
public class OrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private CustomerClient customerClient;

    @Autowired
    private CatalogClient catalogClient;

    @Autowired
    private OrderEventPublisher eventPublisher;

    // -----------------------------------------------------------------------
    // CREATE
    // -----------------------------------------------------------------------

    /**
     * POST /api/orders
     *
     * Body: {"customerId":1,"items":[{"productId":1,"qty":3}]}
     *
     * 201 with the whole order · 400 when the customer or a product does not
     * exist, or the stock is too low · 503 when another service is unreachable.
     */
    @PostMapping("/orders")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO dto) {

        if (dto.getCustomerId() == null || dto.getItems() == null || dto.getItems().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // --- 1. does the customer exist? ask customer-service ---------------
        CustomerDTO customer;
        try {
            customer = customerClient.getCustomer(dto.getCustomerId());
        } catch (FeignException.NotFound e) {
            // Feign does not return an empty Optional for a 404 - it THROWS.
            // 400, because the caller asked for a customer that is not there.
            LOGGER.warn("unknown customer {}", dto.getCustomerId());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // A different failure means a different answer. 503: you asked
            // correctly, I cannot do it right now.
            LOGGER.error("customer-service unavailable: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
        if (customer == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Order order = new Order();
        order.setCustomerId(dto.getCustomerId());
        order.setOrderDate(dto.getOrderDate() == null ? LocalDateTime.now() : dto.getOrderDate());
        order.setStatus(dto.getStatus() == null ? "pending" : dto.getStatus());

        BigDecimal total = BigDecimal.ZERO;
        List<OrderPlacedEvent.Item> eventItems = new ArrayList<>();
        Integer servedBy = null;
        // product names we already fetched, so the response does not ask
        // catalog-service the same question twice
        Map<Long, String> productNames = new HashMap<>();

        // --- 2. price each line from catalog-service ------------------------
        for (OrderItemDTO line : dto.getItems()) {
            if (line.getProductId() == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            int qty = line.getQty() == null ? 1 : line.getQty();
            if (qty <= 0) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            ProductDTO product;
            try {
                product = catalogClient.getProduct(line.getProductId());
            } catch (FeignException.NotFound e) {
                LOGGER.warn("unknown product {}", line.getProductId());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } catch (Exception e) {
                LOGGER.error("catalog-service unavailable: {}", e.getMessage());
                return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
            }
            if (product == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (product.getStock() != null && product.getStock() < qty) {
                LOGGER.warn("not enough stock for product {}: want {}, have {}",
                        product.getId(), qty, product.getStock());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // which catalog instance answered - the load balancer, made visible
            servedBy = product.getServedBy();

            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setQty(qty);
            // the price is COPIED at the time of sale. A client cannot invent
            // its own price, and tomorrow's price change cannot rewrite history.
            item.setUnitPrice(product.getPrice());
            order.addItem(item);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(qty)));
            productNames.put(product.getId(), product.getPlantName());
            eventItems.add(new OrderPlacedEvent.Item(
                    product.getId(), product.getPlantName(), qty, product.getPrice()));
        }

        order.setTotalPrice(total);

        // --- 3. save. One save() writes the order AND its lines, because of
        //        cascade = ALL on the one-to-many.
        Order saved = orderRepository.save(order);

        // --- 4. announce it. Last, so we never announce an order that failed
        //        to save. Nobody is called; whoever subscribed will hear it.
        OrderPlacedEvent event = new OrderPlacedEvent();
        event.setOrderId(saved.getId());
        event.setCustomerId(saved.getCustomerId());
        event.setCustomerName(customer.getUsername());
        event.setTotalPrice(saved.getTotalPrice());
        event.setOrderDate(saved.getOrderDate());
        event.setItems(eventItems);
        eventPublisher.publishOrderPlaced(event);

        return new ResponseEntity<>(
                enrich(saved, customer.getUsername(), servedBy, productNames), HttpStatus.CREATED);
    }

    // -----------------------------------------------------------------------
    // READ
    // -----------------------------------------------------------------------

    /** GET /api/orders - 200 with every order. */
    @GetMapping("/orders")
    public ResponseEntity<List<OrderDTO>> listOrders() {
        List<OrderDTO> dtos = new ArrayList<>();
        for (Order order : orderRepository.findAll()) {
            dtos.add(enrich(order, customerNameOrNull(order.getCustomerId()), null, null));
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    /** GET /api/orders/{id} - 200, or 404. */
    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long id) {
        Optional<Order> order = orderRepository.findById(id);
        if (!order.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(
                enrich(order.get(), customerNameOrNull(order.get().getCustomerId()), null, null),
                HttpStatus.OK);
    }

    // -----------------------------------------------------------------------
    // UPDATE
    // -----------------------------------------------------------------------

    /**
     * PUT /api/orders/{id} - REPLACE. 200, or 404.
     *
     * Whatever the body does not contain is cleared. Compare with PATCH below:
     * the same one-field body sent to each gives the opposite result, and that
     * is exactly why an API needs both verbs.
     */
    @PutMapping("/orders/{id}")
    public ResponseEntity<OrderDTO> replaceOrder(@PathVariable Long id, @RequestBody OrderDTO dto) {
        Optional<Order> found = orderRepository.findById(id);
        if (!found.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Order order = found.get();
        order.setStatus(dto.getStatus());
        order.setOrderDate(dto.getOrderDate());
        order.setTotalPrice(dto.getTotalPrice());

        Order saved = orderRepository.save(order);
        return new ResponseEntity<>(enrich(saved, null, null, null), HttpStatus.OK);
    }

    /**
     * PATCH /api/orders/{id} - MERGE. 200, or 404.
     *
     * Typically {"status":"shipped"}.
     *
     * Loading the existing row first is what makes this a merge. The mapper then
     * copies only the fields the client actually sent, because of the IGNORE
     * strategy on updateOrderFromDto.
     */
    @PatchMapping("/orders/{id}")
    public ResponseEntity<OrderDTO> patchOrder(@PathVariable Long id, @RequestBody OrderDTO dto) {
        Optional<Order> found = orderRepository.findById(id);
        if (!found.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Order order = found.get();
        orderMapper.updateOrderFromDto(dto, order);
        Order saved = orderRepository.save(order);

        return new ResponseEntity<>(
                enrich(saved, customerNameOrNull(saved.getCustomerId()), null, null), HttpStatus.OK);
    }

    // -----------------------------------------------------------------------
    // DELETE
    // -----------------------------------------------------------------------

    /**
     * DELETE /api/orders/{id} - 204, or 404.
     *
     * The lines go with it: OrderItems is a weak entity, and cascade = ALL plus
     * orphanRemoval is the ON DELETE CASCADE of our schema.
     */
    @DeleteMapping("/orders/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        if (!orderRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        orderRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // -----------------------------------------------------------------------
    // helpers
    // -----------------------------------------------------------------------

    /**
     * The mapper copies what it can. The rest - the customer's name, the product
     * names, the line totals, which instance answered - is either arithmetic or
     * comes from another service, so it is filled in here.
     *
     * knownNames holds the product names createOrder already fetched, so the
     * response does not ask catalog-service the same question twice. When it is
     * null (listing, reading, updating) each name is looked up.
     *
     * Worth counting out loud at the demo: ten orders of one line each is ten
     * HTTP calls. A single JOIN did this in the 3-tier version. That is the
     * price of splitting the application up.
     */
    private OrderDTO enrich(Order order, String customerName, Integer servedBy,
                            Map<Long, String> knownNames) {
        OrderDTO dto = orderMapper.toDto(order);
        dto.setCustomerName(customerName);
        dto.setServedBy(servedBy);

        for (OrderItemDTO line : dto.getItems()) {
            if (line.getUnitPrice() != null && line.getQty() != null) {
                line.setLineTotal(line.getUnitPrice().multiply(BigDecimal.valueOf(line.getQty())));
            }
            if (knownNames != null && knownNames.containsKey(line.getProductId())) {
                line.setProductName(knownNames.get(line.getProductId()));
            } else {
                line.setProductName(productNameOrNull(line.getProductId()));
            }
        }
        return dto;
    }

    /**
     * Best-effort product name lookup, for the same reason as
     * customerNameOrNull below: a line we cannot name is still a line that was
     * really sold.
     */
    private String productNameOrNull(Long productId) {
        if (productId == null) {
            return null;
        }
        try {
            ProductDTO product = catalogClient.getProduct(productId);
            return product == null ? null : product.getPlantName();
        } catch (Exception e) {
            LOGGER.warn("no name for product {}: {}", productId, e.getMessage());
            return null;
        }
    }

    /**
     * Best-effort name lookup for listing.
     *
     * A listing that cannot name its customers is still worth returning - the
     * orders we recorded are true either way. So this catches and returns null
     * instead of failing the whole request. That is a different decision from
     * createOrder, where an unchecked customer would make the order worthless.
     * Same failure, two different right answers.
     */
    private String customerNameOrNull(Long customerId) {
        if (customerId == null) {
            return null;
        }
        try {
            CustomerDTO customer = customerClient.getCustomer(customerId);
            return customer == null ? null : customer.getUsername();
        } catch (Exception e) {
            LOGGER.warn("no name for customer {}: {}", customerId, e.getMessage());
            return null;
        }
    }
}
