package xyz.beriholic.beeyes.filter;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;
import xyz.beriholic.beeyes.consts.ContextConst;
import xyz.beriholic.beeyes.entity.dto.UserSession;
import xyz.beriholic.beeyes.helper.ContextHelper;
import xyz.beriholic.beeyes.model.Context;
import xyz.beriholic.beeyes.utils.Const;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
public class RequestLogFilter extends OncePerRequestFilter {
    private final Set<String> ignores = Set.of("/swagger-ui", "/v3/api-docs", "/api/metric/list", "/api/metric/current");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (this.isIgnoreUrl(request.getServletPath())) {
            filterChain.doFilter(request, response);
        } else {
            try {
                long startTime = System.currentTimeMillis();
                this.logRequestStart(request);
                ContentCachingResponseWrapper wrapper = new ContentCachingResponseWrapper(response);
                filterChain.doFilter(request, wrapper);
                this.logRequestEnd(wrapper, startTime);
                wrapper.copyBodyToResponse();
            } finally {
                // 清理 MDC，避免内存泄漏
                MDC.clear();
            }
        }
    }

    private boolean isIgnoreUrl(String url) {
        for (String ignore : ignores) {
            if (url.startsWith(ignore)) return true;
        }
        return false;
    }

    public void logRequestEnd(ContentCachingResponseWrapper wrapper, long startTime) {
        long time = System.currentTimeMillis() - startTime;
        int status = wrapper.getStatus();
        String content = status != 200 ?
                status + " 错误" : new String(wrapper.getContentAsByteArray());
        log.info("请求处理耗时: {}ms | 响应结果: {}", time, content);
    }

    public void logRequestStart(HttpServletRequest request) {
        Long traceId = Long.valueOf(Optional.ofNullable(request.getHeader("BeEyes-Trace")).orElse(IdUtil.getSnowflakeNextIdStr()));
        MDC.put("traceId", String.valueOf(traceId));
        JSONObject object = new JSONObject();
        request.getParameterMap().forEach((k, v) -> object.set(k, v.length > 0 ? v[0] : null));
        Object id = request.getAttribute(Const.ATTR_USER_ID);
        if (id != null) {
            UserSession user = UserSession.get();
            String token = StpUtil.getTokenValue();
            Context context = ContextHelper.getOrCreateContext(request);
            request.setAttribute(ContextConst.CONTEXT_ATTRIBUTE, context);
            log.info("请求URL: \"{}\" ({}) | 远程IP地址: {} │ user: {} id: {} token: {} | 请求参数列表: {}",
                    request.getServletPath(), request.getMethod(), request.getRemoteAddr(),
                    user.getUsername(), id, token, object);
        } else {
            log.info("请求URL: \"{}\" ({}) | 远程IP地址: {} │ 身份: 未验证 | 请求参数列表: {}",
                    request.getServletPath(), request.getMethod(), request.getRemoteAddr(), object);
        }
    }
}
