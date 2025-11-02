package cv.beriholic.beeyes.filter;


import cn.hutool.core.util.IdUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
public class RequestLogFilter extends OncePerRequestFilter {
    private final Set<String> ignores = Set.of("/swagger", "/api/v1/metric/list", "/api/v1/metric/current");

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
        log.info("请求处理耗时: {}ms ", time);
    }

    public void logRequestStart(HttpServletRequest request) {
        buildTrace(request);
    }

    private void buildTrace(HttpServletRequest request) {
        Long traceId = Long.valueOf(Optional.ofNullable(request.getHeader("BeEyes-Trace")).orElse(IdUtil.getSnowflakeNextIdStr()));
        MDC.put("traceId", String.valueOf(traceId));
    }
}