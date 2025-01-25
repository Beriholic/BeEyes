package xyz.beriholic.beeyes.client.entity;

import com.alibaba.fastjson2.JSONObject;

public record Response(
        long id,
        int code,
        Object data,
        String msg
) {
    public static Response onFail(Exception e) {
        return new Response(0, 500, null, e.getMessage());
    }

    public boolean isOk() {
        return code == 200;
    }

    public JSONObject asJson() {
        return JSONObject.from(this.data);
    }

    public String asString() {
        return this.data.toString();
    }
}
