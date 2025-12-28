package cv.beriholic.beeyes.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum AlertMetricType {
    CPU(1, "CPU使用率"),
    MEMORY(2, "内存使用率"),
    DISK(3, "磁盘使用率"),
    STATUS(4, "服务器状态");

    private final Integer key;
    private final String desc;

    public static AlertMetricType of(Integer key) {
        if (Objects.isNull(key)) {
            return null;
        }
        for (AlertMetricType type : AlertMetricType.values()) {
            if (type.getKey().equals(key)) {
                return type;
            }
        }
        return null;
    }
}
