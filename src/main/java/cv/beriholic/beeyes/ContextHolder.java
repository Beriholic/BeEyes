package cv.beriholic.beeyes;

import cv.beriholic.beeyes.models.dto.Context;

public class ContextHolder {
    private static final ThreadLocal<Context> CONTEXT_HOLDER = new ThreadLocal<>();

    public static Context getCurrent() {
        return CONTEXT_HOLDER.get();
    }

    public static void setCurrent(Context context) {
        CONTEXT_HOLDER.set(context);
    }

    public static void clear() {
        CONTEXT_HOLDER.remove();
    }
}

