package xyz.beriholic.beeyes.exception;

public class PasswordError extends RuntimeException {
    public PasswordError() {
      super("密码错误");
    }
}
