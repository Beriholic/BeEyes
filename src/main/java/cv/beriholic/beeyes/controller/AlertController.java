package cv.beriholic.beeyes.controller;

import cv.beriholic.beeyes.consts.PermissionCode;
import cv.beriholic.beeyes.helper.PermissionValidateHelper;
import cv.beriholic.beeyes.models.dto.AlertRuleDTO;
import cv.beriholic.beeyes.models.dto.RestBean;
import cv.beriholic.beeyes.models.entity.AlertRuleDO;
import cv.beriholic.beeyes.models.entity.dto.CreateAlertRequest;
import cv.beriholic.beeyes.models.entity.dto.DeleteAlertRequest;
import cv.beriholic.beeyes.models.entity.dto.UpdateAlertRequest;
import cv.beriholic.beeyes.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/alert")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;
    private final PermissionValidateHelper permissionValidateHelper;

    @PostMapping("/rule/create")
    public RestBean<Void> createRule(@RequestBody CreateAlertRequest request) {
        permissionValidateHelper.checkPermission(PermissionCode.ALERT_MANAGE);
        alertService.createRule(request);
        return RestBean.success();
    }

    @PutMapping("/rule/update")
    public RestBean<Void> updateRule(@RequestBody UpdateAlertRequest request) {
        permissionValidateHelper.checkPermission(PermissionCode.ALERT_MANAGE);
        alertService.updateRule(request);
        return RestBean.success();
    }

    @DeleteMapping("/rule/delete")
    public RestBean<Void> deleteRule(@RequestBody DeleteAlertRequest request) {
        permissionValidateHelper.checkPermission(PermissionCode.ALERT_MANAGE);
        alertService.deleteRule(request);
        return RestBean.success();
    }

    @GetMapping("/rule/list")
    public RestBean<List<AlertRuleDTO>> listRules() {
        permissionValidateHelper.checkPermission(PermissionCode.ALERT_MANAGE);
        List<AlertRuleDO> rules = alertService.getRules();
        List<AlertRuleDTO> dtos = rules.stream().map(AlertRuleDTO::new).collect(Collectors.toList());
        return RestBean.success(dtos);
    }
}
