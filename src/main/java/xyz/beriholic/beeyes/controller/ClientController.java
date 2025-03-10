package xyz.beriholic.beeyes.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import xyz.beriholic.beeyes.entity.RestBean;
import xyz.beriholic.beeyes.entity.dto.Machine;
import xyz.beriholic.beeyes.entity.vo.request.MachineInfoReportVO;
import xyz.beriholic.beeyes.entity.vo.request.RuntimeInfoVO;
import xyz.beriholic.beeyes.service.ClientService;
import xyz.beriholic.beeyes.utils.Const;

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
    public RestBean<Void> reportClientInfo(
            @RequestAttribute(Const.ATTR_CLIENT) Machine machine,
            @RequestBody @Valid MachineInfoReportVO vo
    ) {
        service.reportClientInfo(machine.getId(), vo);
        return RestBean.success();
    }

    @PostMapping("/report/runtime")
    public RestBean<Void> reportRuntimeInfo(
            @RequestAttribute(Const.ATTR_CLIENT) Machine machine,
            @RequestBody @Valid RuntimeInfoVO vo
    ) {
        service.reportRuntimeInfo(machine.getId(), vo);
        return RestBean.success();
    }
}
