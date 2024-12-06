package xyz.beriholic.beeyes.filter;

import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import xyz.beriholic.beeyes.entity.RestBean;
import xyz.beriholic.beeyes.utils.Const;
import xyz.beriholic.beeyes.utils.FlowUtils;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
@Order(Const.ORDER_FLOW_LIMIT)
public class FlowLimitingFilter extends HttpFilter {

    @Resource
    StringRedisTemplate template;

    @Value("${spring.web.flow.limit}")
    int limit;

    @Value("${spring.web.flow.period}")
    int period;

    @Value("${spring.web.flow.block}")
    int block;

    @Resource
    FlowUtils utils;

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String address = request.getRemoteAddr();
        if (!tryCount(address))
            this.writeBlockMessage(response);
        else
            chain.doFilter(request, response);
    }

    private boolean tryCount(String address) {
        synchronized (address.intern()) {
            if (Boolean.TRUE.equals(template.hasKey(Const.FLOW_LIMIT_BLOCK + address)))
                return false;
            String counterKey = Const.FLOW_LIMIT_COUNTER + address;
            String blockKey = Const.FLOW_LIMIT_BLOCK + address;
            return utils.limitPeriodCheck(counterKey, blockKey, block, limit, period);
        }
    }

    private void writeBlockMessage(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write(RestBean.onForbidden("操作频繁，请稍后再试").asJsonString());
    }
}