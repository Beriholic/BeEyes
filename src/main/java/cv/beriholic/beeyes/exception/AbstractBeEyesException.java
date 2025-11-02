package cv.beriholic.beeyes.exception;


import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AbstractBeEyesException extends RuntimeException {
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