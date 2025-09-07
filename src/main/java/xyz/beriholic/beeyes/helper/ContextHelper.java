package xyz.beriholic.beeyes.helper;

import jakarta.servlet.http.HttpServletRequest;
import xyz.beriholic.beeyes.consts.ContextConst;
import xyz.beriholic.beeyes.model.Context;

import java.util.Optional;

public class ContextHelper {
    public static Context getOrCreateContext(HttpServletRequest request) {
        return (Context) Optional.ofNullable(request.getAttribute(ContextConst.CONTEXT_ATTRIBUTE)).orElseGet(() -> {
            Context context = new Context();
            request.setAttribute(ContextConst.CONTEXT_ATTRIBUTE, context);
            return context;
        });
    }

    public static void setContext(HttpServletRequest request, Context context) {
        request.setAttribute(ContextConst.CONTEXT_ATTRIBUTE, context);
    }
}
