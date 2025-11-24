package cv.beriholic.beeyes.config;

import cv.beriholic.beeyes.consts.KafkaTopic;
import cv.beriholic.beeyes.models.dto.MessageEntity;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfiguration {
    @Bean
    public KafkaTemplate<String, MessageEntity> kafkaTemplate(ProducerFactory<String, MessageEntity> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public NewTopic machineRuntimeMetricTopic() {
        return new NewTopic(KafkaTopic.MACHINE_RUNTIME_METRIC, 3, (short) 1);
    }

    @Bean
    public NewTopic serverStatusUpdatedTopic() {
        return new NewTopic(KafkaTopic.SERVER_STATUS_UPDATED, 3, (short) 1);
    }
}

