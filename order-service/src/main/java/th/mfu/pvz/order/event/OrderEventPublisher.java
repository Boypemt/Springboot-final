package th.mfu.pvz.order.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The producer half of the pub/sub. KafkaTemplate is the whole API.
 *
 * Two things to say at the demo:
 *
 * 1. send() does not wait for anybody, and it cannot fail because a subscriber
 *    is down - there are no subscribers as far as this class is concerned. The
 *    event goes to the broker, and the broker keeps it.
 *
 * 2. Publishing is deliberately the LAST step of createOrder. If saving the
 *    order fails we must not announce an order that does not exist.
 */
@Component
public class OrderEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventPublisher.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.kafka.topic:orders}")
    private String topicName;

    /**
     * Spring Boot's own ObjectMapper, injected rather than built with
     * "new ObjectMapper()".
     *
     * This matters: OrderPlacedEvent has a LocalDateTime, and a bare
     * ObjectMapper cannot serialize Java 8 dates - it throws
     * "Java 8 date/time type not supported by default". Boot's instance already
     * has the JavaTimeModule registered, so it writes "2026-08-12T14:03:00".
     */
    @Autowired
    private ObjectMapper objectMapper;

    public void publishOrderPlaced(OrderPlacedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topicName, payload);
            LOGGER.info("published OrderPlaced for order {} to topic {}", event.getOrderId(), topicName);
        } catch (Exception e) {
            // The order is already saved and the customer already has their 201.
            // A broker problem must not turn a successful purchase into an error,
            // so we log it and move on.
            //
            // Log the whole exception, not just getMessage(): a swallowed
            // stack trace here once cost an afternoon, because the publish
            // failed silently while the request still answered 201.
            LOGGER.error("could not publish OrderPlaced for order " + event.getOrderId(), e);
        }
    }
}
