package xyz.beriholic.beeyes.listener;

import jakarta.annotation.Resource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import xyz.beriholic.beeyes.consts.KafkaConst;
import xyz.beriholic.beeyes.entity.dto.WarnMessage;
import xyz.beriholic.beeyes.service.AccountService;
import xyz.beriholic.beeyes.utils.MailUtils;

@Component
public class WamMessageListener {
    @Resource
    AccountService accountService;
    @Resource
    MailUtils mailUtils;


    @KafkaListener(id = KafkaConst.AUTH_MAIL_ID, topics = KafkaConst.AUTH_MAIL_TOPIC)
    public void listenWarmMessage(WarnMessage message) {
        String mail=accountService.getUserMail();
        mailUtils.sendMail(message.getTitle(),message.getContent(),mail);
    }
}
