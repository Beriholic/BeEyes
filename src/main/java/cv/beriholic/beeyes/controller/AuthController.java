package cv.beriholic.beeyes.controller;

import cv.beriholic.beeyes.ContextHolder;
import cv.beriholic.beeyes.aspect.IgnoreContextFill;
import cv.beriholic.beeyes.models.dto.Context;
import cv.beriholic.beeyes.models.dto.RestBean;
import cv.beriholic.beeyes.models.entity.dto.AuthLoginRequest;
import cv.beriholic.beeyes.models.request.AuthChangePasswordRequest;
import cv.beriholic.beeyes.service.AuthService;
import cv.beriholic.beeyes.utils.JsonUtil;
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
        Context context = ContextHolder.getCurrent();
        log.info("Context: {}", JsonUtil.toJSONString(context));
        authService.changePassword(request);
        return RestBean.success();
    }
}
