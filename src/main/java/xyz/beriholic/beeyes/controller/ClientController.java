package xyz.beriholic.beeyes.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.beriholic.beeyes.entity.RestBean;
import xyz.beriholic.beeyes.service.ClientService;

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


}
