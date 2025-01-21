package xyz.beriholic.beeyes.exception;

public class UserNameOrPasswordError extends RuntimeException {
    public UserNameOrPasswordError() {
        super("用户名或密码错误");
    }
}
