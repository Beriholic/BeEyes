package xyz.beriholic.beeyes.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import xyz.beriholic.beeyes.entity.RestBean;
import xyz.beriholic.beeyes.entity.dto.Client;
import xyz.beriholic.beeyes.entity.vo.request.ClientReportVO;
import xyz.beriholic.beeyes.service.ClientService;
import xyz.beriholic.beeyes.utils.Const;

@RestController
@RequestMapping("/client")
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
                ? RestBean.onOk()
                : RestBean.onFail(401, "客户端注册失败，Token无效");
    }

    @PostMapping("/report")
    public RestBean<Void> reportClientInfo(
            @RequestAttribute(Const.ATTR_CLIENT) Client client,
            @RequestBody @Valid ClientReportVO vo
    ) {
        service.reportClientDetail(client.getId(), vo);
        return RestBean.onOk();
    }
}
