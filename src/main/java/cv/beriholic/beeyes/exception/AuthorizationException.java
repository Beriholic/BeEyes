package cv.beriholic.beeyes.exception;


public class AuthorizationException extends AbstractBeEyesException {
    public AuthorizationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthorizationException(Integer code, String msg) {
        super(code, msg);
    }
}