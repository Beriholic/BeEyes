package xyz.beriholic.beeyes.utils;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailUtils {
    @Resource
    JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    String username;
}
