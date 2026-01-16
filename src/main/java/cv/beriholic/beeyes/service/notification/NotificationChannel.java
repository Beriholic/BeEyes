package cv.beriholic.beeyes.service.notification;

import cv.beriholic.beeyes.models.entity.AlertLogDO;
import cv.beriholic.beeyes.models.entity.AlertRuleDO;

public interface NotificationChannel {
    NotificationResult notify(AlertLogDO alertLog, AlertRuleDO rule);
}
