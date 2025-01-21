package xyz.beriholic.beeyes.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import xyz.beriholic.beeyes.entity.RestBean;
import xyz.beriholic.beeyes.exception.LoginFrequencyException;
import xyz.beriholic.beeyes.exception.UserNameOrEmailNotFound;
import xyz.beriholic.beeyes.exception.UserNameOrPasswordError;

@RestControllerAdvice
@Slf4j
public class AuthorizeExceptionController {
    @ExceptionHandler(UserNameOrPasswordError.class)
    public RestBean<Void> userNameOrPasswordError(UserNameOrPasswordError exception) {
        return RestBean.unauthorized(exception.getMessage());
    }

    @ExceptionHandler(UserNameOrEmailNotFound.class)
    public RestBean<Void> userNameOrEmailNotFound(UserNameOrEmailNotFound exception) {
        return RestBean.unauthorized(exception.getMessage());
    }

    @ExceptionHandler(LoginFrequencyException.class)
    public RestBean<Void> loginFrequencyException(LoginFrequencyException exception) {
        return RestBean.forbidden(exception.getMessage());
    }
}
