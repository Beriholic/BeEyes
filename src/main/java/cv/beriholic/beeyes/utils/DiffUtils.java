package cv.beriholic.beeyes.utils;

import java.util.Objects;

public class DiffUtils {
    public static <T> T replaceOrNotNull(T a, T b) {
        if (Objects.isNull(a)) {
            return null;
        }
        if (Objects.isNull(b)) {
            return a;
        }

        return Objects.equals(a, b) ? a : b;
    }
}
