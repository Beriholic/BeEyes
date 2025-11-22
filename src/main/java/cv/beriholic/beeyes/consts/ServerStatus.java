package cv.beriholic.beeyes.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum ServerStatus {
    UNKNOW(0, "未知"),
    REGISTER(1, "已注册"),
    UNREGISTER(2, "未注册"),
    ONLINE(3, "在线"),
    OFFLINE(4, "离线");

    private final Integer key;
    private final String desc;

    public static ServerStatus of(Integer key) {
        if (Objects.isNull(key)) {
            return ServerStatus.UNKNOW;
        }
        for (ServerStatus status : ServerStatus.values()) {
            if (status.getKey().equals(key)) {
                return status;
            }
        }
        return ServerStatus.UNKNOW;
    }
}
