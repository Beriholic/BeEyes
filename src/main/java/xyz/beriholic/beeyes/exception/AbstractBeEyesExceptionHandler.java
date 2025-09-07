package xyz.beriholic.beeyes.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import xyz.beriholic.beeyes.model.RestBean;

@RestControllerAdvice
@Slf4j
public class AbstractBeEyesExceptionHandler {
    @ExceptionHandler(AbstractBeEyesException.class)
    public RestBean<Void> userNameOrPasswordError(AbstractBeEyesException exception) {
        return RestBean.failed(exception);
    }
}
