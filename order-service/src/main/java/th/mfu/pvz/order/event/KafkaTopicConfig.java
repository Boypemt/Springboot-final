package th.mfu.pvz.order.event;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Creates the topic on startup so the demo does not depend on Kafka's
 * auto-creation.
 *
 * A topic is a named channel inside the broker: events go in at one end, and
 * every subscriber group reads them out at the other. 1 partition and 1 replica
 * is right for a single-broker classroom setup.
 */
@Configuration
public class KafkaTopicConfig {

    @Value("${app.kafka.topic:orders}")
    private String topicName;

    @Bean
    public NewTopic ordersTopic() {
        return new NewTopic(topicName, 1, (short) 1);
    }
}
