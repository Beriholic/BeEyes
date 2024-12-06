package xyz.beriholic.beeyes.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import org.slf4j.MDC;

import java.util.Optional;

public record RestBean<T>(long id, int code, T data, String message) {
    public static <T> RestBean<T> onOk(T data) {
        return new RestBean<>(requestId(), 200, data, "请求成功");
    }

    public static <T> RestBean<T> onOk() {
        return onOk(null);
    }

    public static <T> RestBean<T> onForbidden(String message) {
        return onFail(403, message);
    }

    public static <T> RestBean<T> onUnauthorized(String message) {
        return onFail(401, message);
    }

    public static <T> RestBean<T> onFail(int code, String message) {
        return new RestBean<>(requestId(), code, null, message);
    }

    private static long requestId() {
        String requestId = Optional.ofNullable(MDC.get("reqId")).orElse("0");
        return Long.parseLong(requestId);
    }

    public String asJsonString() {
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }
}
