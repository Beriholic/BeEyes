package cv.beriholic.beeyes.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum AlertStatus {
    TRIGGERING(0, "触发中"),
    RESOLVED(1, "已恢复");

    private final Integer key;
    private final String desc;

    public static AlertStatus of(Integer key) {
        if (Objects.isNull(key)) {
            return null;
        }
        for (AlertStatus status : AlertStatus.values()) {
            if (status.getKey().equals(key)) {
                return status;
            }
        }
        return null;
    }
}
