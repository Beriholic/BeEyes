package cv.beriholic.beeyes.controller;

import cn.dev33.satoken.stp.StpUtil;
import cv.beriholic.beeyes.aspect.IgnoreContextFill;
import cv.beriholic.beeyes.helper.ValidateHelper;
import cv.beriholic.beeyes.models.dto.RestBean;
import cv.beriholic.beeyes.models.entity.dto.AuthChangePasswordRequest;
import cv.beriholic.beeyes.models.entity.dto.AuthLoginRequest;
import cv.beriholic.beeyes.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @IgnoreContextFill
    @PostMapping("/login")
    public RestBean<Void> login(@RequestBody AuthLoginRequest request) {
        ValidateHelper.validateAuthLoginRequest(request);
        authService.login(request);
        return RestBean.success();
    }

    @PutMapping("/change/password")
    public RestBean<Void> changePassword(@RequestBody AuthChangePasswordRequest request) {
        authService.changePassword(request);
        return RestBean.success();
    }

    @PutMapping("/logout")
    public RestBean<Void> logout() {
        StpUtil.logout();
        return RestBean.success();
    }
}
