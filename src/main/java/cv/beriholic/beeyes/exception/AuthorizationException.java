package cv.beriholic.beeyes.exception;


import java.io.Serial;

public class AuthorizationException extends AbstractBeEyesException {
    @Serial
    private static final long serialVersionUID = -9122275973799262712L;

    public AuthorizationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthorizationException(Integer code, String msg) {
        super(code, msg);
    }
}