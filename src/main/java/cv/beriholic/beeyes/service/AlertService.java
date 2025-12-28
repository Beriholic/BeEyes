package cv.beriholic.beeyes.service;

import cv.beriholic.beeyes.models.entity.AlertRuleDO;
import cv.beriholic.beeyes.models.entity.dto.CreateAlertRequest;
import cv.beriholic.beeyes.models.entity.dto.DeleteAlertRequest;
import cv.beriholic.beeyes.models.entity.dto.UpdateAlertRequest;

import java.util.List;

public interface AlertService {
    void createRule(CreateAlertRequest request);

    void updateRule(UpdateAlertRequest request);

    void deleteRule(DeleteAlertRequest request);

    List<AlertRuleDO> getRules();

    void checkAllAlerts();
}
