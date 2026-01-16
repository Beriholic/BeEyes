package cv.beriholic.beeyes.service;

import cv.beriholic.beeyes.models.entity.AlertLogDO;
import cv.beriholic.beeyes.models.entity.AlertRuleDO;
import cv.beriholic.beeyes.models.entity.dto.CreateAlertRequest;
import cv.beriholic.beeyes.models.entity.dto.DeleteAlertRequest;
import cv.beriholic.beeyes.models.entity.dto.QueryAlertLogRequest;
import cv.beriholic.beeyes.models.entity.dto.UpdateAlertRequest;

import java.util.List;
import java.util.Map;

public interface AlertService {
    void createRule(CreateAlertRequest request);

    void updateRule(UpdateAlertRequest request);

    void deleteRule(DeleteAlertRequest request);

    List<AlertRuleDO> getRules();

    List<AlertLogDO> getAlertLogs(QueryAlertLogRequest request);

    Map<String, Long> getAlertStats();

    void checkAllAlerts();

    void triggerManualCheck();
}
