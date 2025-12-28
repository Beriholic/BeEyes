package cv.beriholic.beeyes.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum AlertCondition {
    GT(1, "大于"),
    LT(2, "小于"),
    EQ(3, "等于");

    private final Integer key;
    private final String desc;

    public static AlertCondition of(Integer key) {
        if (Objects.isNull(key)) {
            return null;
        }
        for (AlertCondition condition : AlertCondition.values()) {
            if (condition.getKey().equals(key)) {
                return condition;
            }
        }
        return null;
    }
}
