package xyz.beriholic.beeyes.model;

import org.slf4j.MDC;
import xyz.beriholic.beeyes.exception.AbstractBeEyesException;
import xyz.beriholic.beeyes.exception.ErrorCode;
import xyz.beriholic.beeyes.utils.JsonUtils;

import java.util.Optional;

public record RestBean<T>(long id, int code, T data, String message) {
    public static <T> RestBean<T> success(T data) {
        return new RestBean<>(requestId(), 0, data, "请求成功");
    }

    public static <T> RestBean<T> success() {
        return success(null);
    }

    public static <T> RestBean<T> failed(int code, String message) {
        return new RestBean<>(requestId(), code, null, message);
    }

    public static <T> RestBean<T> failed(AbstractBeEyesException exception) {
        return failed(exception.getCode(), exception.getMsg());
    }

    public static <T> RestBean<T> failed(ErrorCode errorCode) {
        return failed(errorCode.getCode(), errorCode.getMsg());
    }

    private static long requestId() {
        String requestId = Optional.ofNullable(MDC.get("traceId")).orElse("0");
        return Long.parseLong(requestId);
    }

    public String asJsonString() {
        return JsonUtils.toJson(this);
    }
}
