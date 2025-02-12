package xyz.beriholic.beeyes.exception.controller;

import cn.dev33.satoken.exception.NotLoginException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import xyz.beriholic.beeyes.entity.RestBean;

@Slf4j
@RestControllerAdvice
public class ValidationController {

    @ExceptionHandler(ValidationException.class)
    public RestBean<Void> validateError(ValidationException exception) {
        log.warn("Resolved [{}: {}]", exception.getClass().getName(), exception.getMessage());
        return RestBean.failed(400, "请求参数有误");
    }


    @ExceptionHandler(NotLoginException.class)
    public RestBean<Void> notLogin(NotLoginException exception) {
        return RestBean.failed(401, "未登录");
    }
}
