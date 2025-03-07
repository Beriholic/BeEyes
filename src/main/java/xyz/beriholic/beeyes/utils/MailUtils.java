package xyz.beriholic.beeyes.utils;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailUtils {
    @Resource
    JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    String username;

    public void sendMail(String title,String content,String email){
        SimpleMailMessage message=new SimpleMailMessage();
        message.setSubject(title);
        message.setText(content);
        message.setTo(email);
        message.setFrom(username);

        mailSender.send(message);
    }
}
