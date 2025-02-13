package xyz.beriholic.beeyes.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import xyz.beriholic.beeyes.utils.Const;

import java.io.IOException;

@Component
@Order(Const.ORDER_CORS)
public class CorsFilter extends HttpFilter {
//    @Value("${spring.web.cors.origin}")
//    String origin;
//
//    @Value("${spring.web.cors.credentials}")
//    boolean credentials;
//
//    @Value("${spring.web.cors.methods}")
//    String methods;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;


        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type,jinyum,last-event-id,Cache-Control,Connection");

        if (((HttpServletRequest) req).getMethod().equals("OPTIONS")) {
            response.getWriter().println("ok");
            return;
        }

        chain.doFilter(req, res);
    }
}
