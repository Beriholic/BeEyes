package cv.beriholic.beeyes.models.dto;

import cv.beriholic.beeyes.exception.AbstractBeEyesException;
import cv.beriholic.beeyes.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestBean<T> {
    private int code;
    private String msg;
    private T data;


    public static <T> RestBean<T> success(T data) {
        return new RestBean<>(0, "请求成功", data);
    }

    public static <T> RestBean<T> success() {
        return success(null);
    }

    public static <T> RestBean<T> failed(int code, String msg) {
        return new RestBean<>(code, msg, null);
    }

    public static <T> RestBean<T> failed(AbstractBeEyesException exception) {
        return failed(exception.getCode(), exception.getMsg());
    }

    public static <T> RestBean<T> failed(ErrorCode code) {
        return failed(code.getCode(), code.getMsg());
    }

    public static <T> RestBean<T> failure(int code, String msg) {
        return new RestBean<>(code, msg, null);
    }

    public static <T> RestBean<T> failure(ErrorCode errorCode) {
        return failure(errorCode.getCode(), errorCode.getMsg());
    }
}