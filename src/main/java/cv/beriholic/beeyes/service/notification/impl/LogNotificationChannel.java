package cv.beriholic.beeyes.service.notification.impl;

import cv.beriholic.beeyes.models.entity.AlertLogDO;
import cv.beriholic.beeyes.models.entity.AlertRuleDO;
import cv.beriholic.beeyes.service.notification.NotificationChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LogNotificationChannel implements NotificationChannel {
    @Override
    public void notify(AlertLogDO alertLog, AlertRuleDO rule) {
        log.info("[Alert Notification] Rule: {}, Server: {}, Type: {}, Value: {}, Status: {}",
                rule.name(), alertLog.serverId(), rule.metricType(), alertLog.metricValue(), alertLog.status());
    }
}
