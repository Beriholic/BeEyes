package cv.beriholic.beeyes.controller;

import cn.dev33.satoken.stp.StpUtil;
import cv.beriholic.beeyes.consts.PermissionCode;
import cv.beriholic.beeyes.helper.PermissionValidateHelper;
import cv.beriholic.beeyes.helper.ValidateHelper;
import cv.beriholic.beeyes.models.dto.PageDTO;
import cv.beriholic.beeyes.models.dto.RestBean;
import cv.beriholic.beeyes.models.entity.dto.MachineTerminalListView;
import cv.beriholic.beeyes.models.entity.dto.QueryMachineTerminalListRequest;
import cv.beriholic.beeyes.models.entity.dto.UpdateSSHConfigRequest;
import cv.beriholic.beeyes.service.MachineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/terminal")
@RequiredArgsConstructor
public class TerminalController {
    private final MachineService machineService;
    private final PermissionValidateHelper permissionValidateHelper;

    @PutMapping("/ssh-config/update")
    public RestBean<Void> updateSSHConfig(@RequestBody UpdateSSHConfigRequest request) {
        long userId = StpUtil.getLoginIdAsLong();
        permissionValidateHelper.checkPermission(userId, PermissionCode.SSH_CONNECT);
        ValidateHelper.validateUpdateSSHConfigRequest(request);
        machineService.updateMachineSSHConfig(userId, request);
        return RestBean.success();
    }

    @GetMapping("/list")
    public RestBean<PageDTO<List<MachineTerminalListView>>> queryTerminalList(QueryMachineTerminalListRequest request) {
        long userId = StpUtil.getLoginIdAsLong();
        permissionValidateHelper.checkPermission(userId, PermissionCode.SSH_CONNECT);
        PageDTO<List<MachineTerminalListView>> pageDTO = machineService.queryMachineTerminalList(request);
        return RestBean.success(pageDTO);
    }
}
