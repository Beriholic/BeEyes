package xyz.beriholic.beeyes.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import xyz.beriholic.beeyes.model.RestBean;

@RestControllerAdvice
@Slf4j
public class MethodArgumentNotValidExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public RestBean<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.warn("Resolved [{}: {}]", exception.getClass().getName(), exception.getMessage());
        return RestBean.failed(400, "请求参数有误");
    }
}
