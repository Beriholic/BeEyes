package cv.beriholic.beeyes.utils;

import org.slf4j.MDC;

public class MDCUtil {
    private static final String TRACE_ID = "traceId";

    public static String getTraceId() {
        return MDC.get(TRACE_ID);
    }

    public static void setTraceId(String traceId) {
        MDC.put(TRACE_ID, traceId);
    }
}
