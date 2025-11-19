package cv.beriholic.beeyes.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ServerStatus {
    UNKNOW(0, "未知"),
    REGISTER(1, "已注册");

    private final Integer key;
    private final String desc;
}
