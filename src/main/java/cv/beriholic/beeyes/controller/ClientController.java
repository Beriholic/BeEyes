package cv.beriholic.beeyes.controller;

import cv.beriholic.beeyes.consts.RequestAttributeConst;
import cv.beriholic.beeyes.exception.ErrorCode;
import cv.beriholic.beeyes.models.dto.RestBean;
import cv.beriholic.beeyes.models.dto.system.MachineInfo;
import cv.beriholic.beeyes.models.dto.system.RuntimeInfo;
import cv.beriholic.beeyes.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
@Slf4j
public class ClientController {
    private final ClientService clientService;

    @PostMapping("/register")
    public RestBean<Void> registerMachine(@RequestHeader("Authorization") String authorization) {
        boolean pass = clientService.verifyAndRegister(authorization);
        if (pass) {
            return RestBean.success();
        }
        return RestBean.failed(ErrorCode.UNAUTHORIZED);
    }

    @PostMapping("/report/machine")
    public RestBean<Void> reportMachine(
            @RequestAttribute(RequestAttributeConst.CLIENT_MACHINE_ID) Long machineId,
            @RequestBody MachineInfo machineInfo
    ) {
        clientService.reportMachineInfo(machineId, machineInfo);
        return RestBean.success();
    }

    @PostMapping("/report/runtime")
    public RestBean<Void> reportRuntimeInfo(
            @RequestAttribute(RequestAttributeConst.CLIENT_MACHINE_ID) Long machineId,
            @RequestBody RuntimeInfo runtimeInfo
    ) {
        clientService.reportRuntimeInfo(machineId, runtimeInfo);
        return RestBean.success();
    }
}
