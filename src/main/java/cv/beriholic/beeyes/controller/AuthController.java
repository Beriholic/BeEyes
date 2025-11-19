package cv.beriholic.beeyes.controller;

import cv.beriholic.beeyes.aspect.IgnoreContextFill;
import cv.beriholic.beeyes.models.dto.RestBean;
import cv.beriholic.beeyes.models.request.auth.AuthChangePasswordRequest;
import cv.beriholic.beeyes.models.request.auth.AuthLoginRequest;
import cv.beriholic.beeyes.service.AuthService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/")
public class AuthController {
    @Resource
    private AuthService authService;

    @IgnoreContextFill
    @PostMapping("/auth/login")
    public RestBean<Void> login(@RequestBody AuthLoginRequest request) {
        authService.login(request);
        return RestBean.success();
    }

    @PutMapping("/auth/change/password")
    public RestBean<Void> changePassword(@RequestBody AuthChangePasswordRequest request) {
        authService.changePassword(request);
        return RestBean.success();
    }
}
