package xyz.beriholic.beeyes.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.beriholic.beeyes.consts.KafkaConst;

@Configuration
public class KafkaConfiguration {
    @Bean
    public NewTopic authMailTopic() {
        return new NewTopic(KafkaConst.AUTH_MAIL_TOPIC, 1, (short) 1);
    }

    @Bean
    public NewTopic warnMailTopic() {
        return new NewTopic(KafkaConst.WARN_MAIL_TOPIC, 1, (short) 1);
    }
}
