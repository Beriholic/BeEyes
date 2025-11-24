package cv.beriholic.beeyes.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum ServerStatus {
    ONLINE(0, "在线"),
    OFFLINE(1, "离线"),
    REGISTER(2, "已注册"),
    UNREGISTER(3, "未注册"),
    UNKNOW(4, "未知");

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
