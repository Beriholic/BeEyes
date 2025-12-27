package cv.beriholic.beeyes.controller;

import cn.dev33.satoken.stp.StpUtil;
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

    @PutMapping("/ssh-config/update")
    public RestBean<Void> updateSSHConfig(@RequestBody UpdateSSHConfigRequest request) {
        ValidateHelper.validateUpdateSSHConfigRequest(request);
        Long userId = StpUtil.getLoginIdAsLong();
        machineService.updateMachineSSHConfig(userId, request);
        return RestBean.success();
    }

    @GetMapping("/list")
    public RestBean<PageDTO<List<MachineTerminalListView>>> queryTerminalList(QueryMachineTerminalListRequest request) {
        PageDTO<List<MachineTerminalListView>> pageDTO = machineService.queryMachineTerminalList(request);
        return RestBean.success(pageDTO);
    }
}
