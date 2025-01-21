package xyz.beriholic.beeyes.exception;

public class UserNameOrEmailNotFound extends RuntimeException {
    public UserNameOrEmailNotFound() {
        super("用户名或邮箱不存在");
    }
}
