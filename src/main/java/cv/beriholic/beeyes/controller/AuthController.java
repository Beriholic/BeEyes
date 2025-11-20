package cv.beriholic.beeyes.controller;

import cv.beriholic.beeyes.aspect.IgnoreContextFill;
import cv.beriholic.beeyes.models.dto.RestBean;
import cv.beriholic.beeyes.models.request.auth.AuthChangePasswordRequest;
import cv.beriholic.beeyes.models.request.auth.AuthLoginRequest;
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
        authService.login(request);
        return RestBean.success();
    }

    @PutMapping("/change/password")
    public RestBean<Void> changePassword(@RequestBody AuthChangePasswordRequest request) {
        authService.changePassword(request);
        return RestBean.success();
    }
}
