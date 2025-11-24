package cv.beriholic.beeyes.mq;

import cv.beriholic.beeyes.models.dto.MessageEntity;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Slf4j
public abstract class BaseProducerService {
    @Resource
    private KafkaTemplate<String, MessageEntity> kafkaTemplate;

    protected void sendMessage(String topic, MessageEntity message) {
        if (StringUtils.isNotEmpty(message.getBusinessId())) {
            kafkaTemplate.send(topic, message.getBusinessId(), message);
            return;
        }
        kafkaTemplate.send(topic, message);
    }

    protected void sendMessageWithCallback(String topic, MessageEntity message, Runnable onSuccess, Consumer<Throwable> onFailed) {
        CompletableFuture<SendResult<String, MessageEntity>> future;

        if (StringUtils.isNotEmpty(message.getBusinessId())) {
            future = kafkaTemplate.send(topic, message.getBusinessId(), message);
        } else {
            future = kafkaTemplate.send(topic, message);
        }

        future.whenComplete((result, throwable) -> {
            if (throwable == null) {
                log.info("Message sent successfully, topic: {}, partition: {}, offset: {}",
                        topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                if (onSuccess != null) {
                    onSuccess.run();
                }
            } else {
                log.error("Failed to send message, topic: {}, error: {}", topic, throwable.getMessage());
                if (onFailed != null) {
                    onFailed.accept(throwable);
                }
            }
        });
    }
}
