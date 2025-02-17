package xyz.beriholic.beeyes.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import xyz.beriholic.beeyes.entity.RestBean;
import xyz.beriholic.beeyes.entity.dto.Machine;
import xyz.beriholic.beeyes.service.ClientService;
import xyz.beriholic.beeyes.utils.Const;

import java.io.IOException;

@Slf4j
@Configuration
public class SaTokenConfiguration implements WebMvcConfigurer {
    @Resource
    private HttpServletRequest request;
    @Resource
    private HttpServletResponse response;
    @Resource
    private ClientService clientService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(_ -> {
            SaRouter.match("/api/client/**", _ -> {
                String authorization = request.getHeader("Authorization");

                String uri = request.getRequestURI();
                if (uri.startsWith("/api/client")) {
                    if (!uri.endsWith("/api/client/register")) {
                        Machine machine = clientService.getClientByToken(authorization);
                        if (machine == null) {
                            try {
                                response.setStatus(401);
                                response.getWriter().write(RestBean.failed(401, "主机未注册").asJsonString());
                            } catch (IOException e) {
                                log.error("响应客户端失败", e);
                                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            }
                        } else {
                            request.setAttribute(Const.ATTR_CLIENT, machine);
                        }
                    }
                }
            });

            SaRouter.match("/**")
                    .notMatch("/api/auth/login", "/api/auth/register", "/error")
                    .notMatch("/swagger-ui/**", "/v3/api-docs/**")
                    .notMatch("/api/client/**")
                    .check(_ -> StpUtil.checkLogin());


        })).addPathPatterns("/**");
    }

}
