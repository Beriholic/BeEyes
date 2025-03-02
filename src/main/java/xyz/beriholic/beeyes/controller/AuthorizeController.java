package xyz.beriholic.beeyes.controller;

import cn.dev33.satoken.stp.StpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.beriholic.beeyes.entity.RestBean;
import xyz.beriholic.beeyes.entity.vo.request.ConfirmResetVO;
import xyz.beriholic.beeyes.entity.vo.request.EmailResetVO;
import xyz.beriholic.beeyes.entity.vo.response.AuthorizeVO;
import xyz.beriholic.beeyes.service.AccountService;

import java.util.function.Supplier;

@Validated
@RestController
@RequestMapping("/api/auth")
@Tag(name = "登录校验相关", description = "包括用户登录、注册、验证码请求等操作。")
public class AuthorizeController {
    @Resource
    AccountService accountService;

    @PostMapping("/login")
    @Operation(summary = "登陆")
    public RestBean<AuthorizeVO> login(
            @RequestParam String username,
            @RequestParam String password
    ) {
        AuthorizeVO vo = accountService.authenticate(username, password);

        return RestBean.success(vo);
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public RestBean<Void> logout() {
        StpUtil.logout();
        return RestBean.success();
    }

    @PostMapping("/ping")
    public RestBean<Boolean> ping() {
        return RestBean.success(StpUtil.isLogin());
    }
}
