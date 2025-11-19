package cv.beriholic.beeyes.utils;

public class ValueUtils {
    public static <T> T getOrDefault(T data, T defaultValue) {
        return data == null ? defaultValue : data;
    }
}
