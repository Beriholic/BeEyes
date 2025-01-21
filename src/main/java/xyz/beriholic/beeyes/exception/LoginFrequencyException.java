package xyz.beriholic.beeyes.exception;

public class LoginFrequencyException extends RuntimeException {
    public LoginFrequencyException() {
        super("登录验证频繁，请稍后再试");
    }
}
