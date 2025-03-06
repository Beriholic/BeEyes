package xyz.beriholic.beeyes.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import xyz.beriholic.beeyes.consts.KafkaConst;
import xyz.beriholic.beeyes.entity.dto.WarnMessage;

@Component
public class WamMessageListener {

    @KafkaListener(id = KafkaConst.AUTH_MAIL_ID, topics = KafkaConst.AUTH_MAIL_TOPIC)
    public void listenWarmMessage(WarnMessage message) {
        //TODO send mail
        System.out.println(message);
    }
}
