package cv.beriholic.beeyes.exception;

import java.io.Serial;

public class BizRuntimeException extends AbstractBeEyesException {
    @Serial
    private static final long serialVersionUID = -1946246528809646795L;

    public BizRuntimeException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BizRuntimeException(Integer code, String msg) {
        super(code, msg);
    }
}
