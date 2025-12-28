package cv.beriholic.beeyes.service.notification.impl;

import cv.beriholic.beeyes.models.entity.AlertLogDO;
import cv.beriholic.beeyes.models.entity.AlertRuleDO;
import cv.beriholic.beeyes.service.notification.NotificationChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailNotificationChannel implements NotificationChannel {
    @Override
    public void notify(AlertLogDO alertLog, AlertRuleDO rule) {
        // TODO: Integrate with JavaMailSender
        log.info("[Email Notification] Sending email for alert: {}", rule.name());
    }
}
