package cv.beriholic.beeyes.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HistoryTimeUnit {
    MINUTE(0, "m"),
    HOUR(1, "h"),
    DAY(2, "d"),
    WEEK(3, "w"),
    MONTH(4, "mo");
    private final int value;
    private final String unit;

    public static HistoryTimeUnit of(int timeUnit) {
        for (HistoryTimeUnit value : values()) {
            if (value.value == timeUnit) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown time unit " + timeUnit);
    }
}
