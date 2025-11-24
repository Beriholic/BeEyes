package cv.beriholic.beeyes.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cv.beriholic.beeyes.models.dto.RestBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public RestBean<Void> notLogin() {
        return RestBean.failed(ErrorCode.UNAUTHORIZED);
    }
}
