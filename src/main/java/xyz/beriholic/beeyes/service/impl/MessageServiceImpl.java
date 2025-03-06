package xyz.beriholic.beeyes.service.impl;

import jakarta.annotation.Resource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import xyz.beriholic.beeyes.consts.KafkaConst;
import xyz.beriholic.beeyes.entity.dto.WarnMessage;
import xyz.beriholic.beeyes.service.MessageService;

@Service
public class MessageServiceImpl implements MessageService {
    @Resource
    KafkaTemplate<String, Object> kafkaTemplate;


    @Override
    public void sendWarmMessage(WarnMessage message) {
        kafkaTemplate.send(KafkaConst.WARN_MAIL_TOPIC, message);
    }
}
