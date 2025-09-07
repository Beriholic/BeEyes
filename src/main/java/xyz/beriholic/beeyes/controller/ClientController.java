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
import xyz.beriholic.beeyes.utils.JsonUtils;

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
        log.info("[registerClient] biz start token: {}", token);
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
        log.info("[reportMachineInfo] biz start context: {}, request: {}", JsonUtils.toJson(context), JsonUtils.toJson(request));
        //TODO
        //service.reportClientInfo(machine.getId(), vo);
        return RestBean.success();
    }

    @PostMapping("/report/runtime")
    public RestBean<Void> reportRuntimeInfo(
            @RequestAttribute(ContextConst.CONTEXT_ATTRIBUTE) Context context,
            @RequestBody @Valid ReportRuntimeInfoRequest request
    ) {
        log.info("[reportRuntimeInfo] biz start, context: {}, request: {}", JsonUtils.toJson(context), JsonUtils.toJson(request));
        //TODO
        //service.reportRuntimeInfo(machine.getId(), vo);
        log.info("context: {}, request: {}", context, request);
        return RestBean.success();
    }
}
