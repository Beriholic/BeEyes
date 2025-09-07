package xyz.beriholic.beeyes.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import xyz.beriholic.beeyes.consts.ContextConst;
import xyz.beriholic.beeyes.model.Context;
import xyz.beriholic.beeyes.model.RestBean;
import xyz.beriholic.beeyes.model.request.ReportMachineInfoRequest;
import xyz.beriholic.beeyes.model.request.ReportRuntimeInfoRequest;
import xyz.beriholic.beeyes.service.ClientService;

@Slf4j
@RestController
@RequestMapping("/api/client")
public class ClientController {
    @Resource
    ClientService service;

    @PostMapping("/register")
    public RestBean<Void> registerClient(
            @RequestHeader("Authorization") String token
    ) {
        boolean ok = service.verifyAndRegister(token);

        if (!ok) {
            log.error("客户端注册失败，Token无效: {}", token);
            return RestBean.failed(401, "客户端注册失败，Token无效");
        }
        return RestBean.success();
    }

    @PostMapping("/report/machine")
    public RestBean<Void> reportMachineInfo(
            @RequestAttribute(ContextConst.CONTEXT_ATTRIBUTE) Context context,
            @RequestBody @Valid ReportMachineInfoRequest request
    ) {
        //TODO
//        service.reportClientInfo(machine.getId(), vo);
        return RestBean.success();
    }

    @PostMapping("/report/runtime")
    public RestBean<Void> reportRuntimeInfo(
            @RequestAttribute(ContextConst.CONTEXT_ATTRIBUTE) Context context,
            @RequestBody @Valid ReportRuntimeInfoRequest request
    ) {
        //TODO
//        service.reportRuntimeInfo(machine.getId(), vo);
        return RestBean.success();
    }
}
