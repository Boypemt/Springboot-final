package th.mfu.pvz.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import th.mfu.pvz.order.client.CatalogClient;
import th.mfu.pvz.order.client.CustomerClient;
import th.mfu.pvz.order.domain.Order;
import th.mfu.pvz.order.dto.CustomerDTO;
import th.mfu.pvz.order.dto.ProductDTO;
import th.mfu.pvz.order.repository.OrderRepository;

/**
 * Controller tests for order-service.
 *
 * The other services and the Kafka broker are NOT running during a unit test,
 * so all three are replaced with @MockBean:
 *
 *   CustomerClient / CatalogClient - we decide what the other services "answer"
 *   KafkaTemplate                  - we assert that we tried to publish
 *
 * That is the point of talking to other services through an interface: the
 * interface can be faked.
 *
 * eureka.client.enabled=false stops the test trying to register with a naming
 * server that is not running.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false"
})
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @MockBean
    private CustomerClient customerClient;

    @MockBean
    private CatalogClient catalogClient;

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Long CUSTOMER_ID = 1L;
    private static final Long PRODUCT_ID = 10L;

    /** The happy-path answers from the other two services. */
    @BeforeEach
    public void mockTheOtherServices() {
        CustomerDTO customer = new CustomerDTO();
        customer.setId(CUSTOMER_ID);
        customer.setUsername("crazydave");
        customer.setEmail("dave@pvz.com");
        when(customerClient.getCustomer(CUSTOMER_ID)).thenReturn(customer);

        ProductDTO product = new ProductDTO();
        product.setId(PRODUCT_ID);
        product.setPlantId(5L);
        product.setPlantName("Peashooter");
        product.setPrice(new BigDecimal("100.00"));
        product.setStock(25);
        product.setServedBy(8100);
        when(catalogClient.getProduct(PRODUCT_ID)).thenReturn(product);
    }

    private String orderBody(int qty) {
        return "{\"customerId\":" + CUSTOMER_ID + ","
                + "\"items\":[{\"productId\":" + PRODUCT_ID + ",\"qty\":" + qty + "}]}";
    }

    private Long idOf(MvcResult result) throws Exception {
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asLong();
    }

    private MvcResult createOrder(int qty) throws Exception {
        return mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderBody(qty)))
                .andExpect(status().isCreated())
                .andReturn();
    }

    // -----------------------------------------------------------------------

    /**
     * CREATE. One POST must: call both services, price the line from the
     * catalog, save the order and its item, and publish the Kafka event.
     */
    @Test
    public void createOrderReturns201AndPublishesTheEvent() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderBody(3)))
                .andExpect(status().isCreated())
                // the name came from customer-service, not from our database
                .andExpect(jsonPath("$.customerName").value("crazydave"))
                // the price came from catalog-service, not from the request
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].productName").value("Peashooter"))
                .andExpect(jsonPath("$.items[0].unitPrice").value(100.00))
                .andExpect(jsonPath("$.items[0].lineTotal").value(300.00))
                .andExpect(jsonPath("$.totalPrice").value(300.00))
                .andExpect(jsonPath("$.status").value("pending"))
                // which catalog instance answered - the load balancer
                .andExpect(jsonPath("$.servedBy").value(8100))
                .andReturn();

        // the item was cascaded with the order by a single save()
        Optional<Order> saved = orderRepository.findById(idOf(result));
        assertTrue(saved.isPresent(), "the order was not saved");
        assertEquals(1, saved.get().getItems().size(), "the item was not cascaded");

        // and the announcement went out
        verify(kafkaTemplate).send(eq("orders"), anyString());
    }

    /** LIST. */
    @Test
    public void listOrdersReturnsTheCreatedOrder() throws Exception {
        Long id = idOf(createOrder(1));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.id == " + id + ")]").exists());
    }

    /**
     * UPDATE (PATCH) - a partial update. Sending only the status must not wipe
     * the total or the lines. This is the test for the IGNORE strategy.
     */
    @Test
    public void patchOrderChangesOnlyTheFieldsSent() throws Exception {
        Long id = idOf(createOrder(3));

        mockMvc.perform(patch("/api/orders/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"shipped\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("shipped"))
                // everything the client did not send is still there
                .andExpect(jsonPath("$.totalPrice").value(300.00))
                .andExpect(jsonPath("$.customerId").value(CUSTOMER_ID))
                .andExpect(jsonPath("$.items.length()").value(1));
    }

    /** UPDATE (PUT) - a full replace, so the missing fields ARE cleared. */
    @Test
    public void putOrderReplacesTheResource() throws Exception {
        Long id = idOf(createOrder(3));

        mockMvc.perform(put("/api/orders/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"cancelled\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("cancelled"))
                // the same one-field body that PATCH merged, PUT wiped
                .andExpect(jsonPath("$.totalPrice").doesNotExist());
    }

    /** DELETE - 204, then gone. */
    @Test
    public void deleteOrderRemovesItAndUnknownIdIs404() throws Exception {
        Long id = idOf(createOrder(1));

        mockMvc.perform(delete("/api/orders/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/orders/" + id))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/orders/" + id))
                .andExpect(status().isNotFound());
    }

    /**
     * A customer who does not exist is the CALLER's mistake: 400, not 500.
     * And nothing must be announced, because no order was placed.
     */
    @Test
    public void createOrderWithUnknownCustomerReturns400() throws Exception {
        when(customerClient.getCustomer(999L)).thenReturn(null);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerId\":999,\"items\":[{\"productId\":" + PRODUCT_ID + ",\"qty\":1}]}"))
                .andExpect(status().isBadRequest());

        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    /** Ordering more than the shop has is also a 400. */
    @Test
    public void createOrderWithTooLittleStockReturns400() throws Exception {
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderBody(9999)))
                .andExpect(status().isBadRequest());

        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    /** An unknown product is a 400 too. */
    @Test
    public void createOrderWithUnknownProductReturns400() throws Exception {
        when(catalogClient.getProduct(anyLong())).thenReturn(null);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerId\":" + CUSTOMER_ID + ",\"items\":[{\"productId\":404,\"qty\":1}]}"))
                .andExpect(status().isBadRequest());
    }
}
