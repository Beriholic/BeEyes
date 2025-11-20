package cv.beriholic.beeyes.exception;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Data
public class AbstractBeEyesException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -6383913943481081658L;

    private Integer code;
    private String msg;

    public AbstractBeEyesException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
    }

    public AbstractBeEyesException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}