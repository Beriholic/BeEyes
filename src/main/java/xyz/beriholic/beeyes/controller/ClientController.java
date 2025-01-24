package xyz.beriholic.beeyes.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import xyz.beriholic.beeyes.entity.RestBean;
import xyz.beriholic.beeyes.entity.dto.Client;
import xyz.beriholic.beeyes.entity.vo.request.ClientReportVO;
import xyz.beriholic.beeyes.entity.vo.request.RuntimeInfoVO;
import xyz.beriholic.beeyes.service.ClientService;
import xyz.beriholic.beeyes.utils.Const;

@RestController
@RequestMapping("/api/client")
public class ClientController {
    @Resource
    ClientService service;

    /**
     * 客户端服务注册
     *
     * @param token Token
     * @return 是否操作成功
     */
    @GetMapping("/register")
    public RestBean<Void> registerClient(
            @RequestHeader("Authorization") String token
    ) {
        System.out.println(token);
        return service.verifyAndRegister(token)
                ? RestBean.success()
                : RestBean.failed(401, "客户端注册失败，Token无效");
    }

    /**
     * 客户端数据上报
     *
     * @param client 客户端
     * @param vo     客户端数据
     * @return 是否操作成功
     */
    @PostMapping("/report")
    public RestBean<Void> reportClientInfo(
            @RequestAttribute(Const.ATTR_CLIENT) Client client,
            @RequestBody @Valid ClientReportVO vo
    ) {
        service.reportClientInfo(client.getId(), vo);
        return RestBean.success();
    }

    /**
     * 客户端运行时数据上报
     *
     * @param client 客户端
     * @param vo     运行数据
     * @return 是否操作成功
     */
    @PostMapping("/runtime")
    public RestBean<Void> reportRuntimeInfo(
            @RequestAttribute(Const.ATTR_CLIENT) Client client,
            @RequestBody @Valid RuntimeInfoVO vo
    ) {
        service.reportRuntimeInfo(client.getId(), vo);
        return RestBean.success();
    }
}
