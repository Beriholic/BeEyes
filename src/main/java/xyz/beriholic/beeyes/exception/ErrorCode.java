package xyz.beriholic.beeyes.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    SUCCESS(0, "成功"),
    //10000 登陆错误
    USER_NAME_OR_EMAIL_NOT_FOUND(10000, "用户名或邮箱不存在"),
    PASSWORD_ERROR(10001, "密码错误"),
    //20000 限流
    REQUEST_FREQUENCY(20000, "请求频繁，请稍后再试"),
    LOGIN_FREQUENCY(20002, "登录验证频繁，请稍后再试");

    private final Integer code;
    private final String msg;
}
