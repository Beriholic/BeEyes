package cv.beriholic.beeyes.config;

//import cn.dev33.satoken.router.SaRouter;
//import cn.dev33.satoken.stp.StpUtil;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//
//override fun addInterceptors(registry:InterceptorRegistry) {
//    registry.addInterceptor(SaInterceptor {
//        SaRouter.match("/**")
//                .notMatch("/swagger-ui/**", "/v3/api-docs/**")
//                .notMatchMethod("OPTIONS").check { r ->
//                StpUtil.checkLogin()
//        }
//    }).addPathPatterns("/**")
//}


import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cv.beriholic.beeyes.consts.RequestAttributeConst;
import cv.beriholic.beeyes.exception.ErrorCode;
import cv.beriholic.beeyes.models.dto.RestBean;
import cv.beriholic.beeyes.service.ClientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.util.Objects;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class SaTokenConfiguration implements WebMvcConfigurer {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ClientService clientService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(interceptor -> {
            SaRouter.match("/api/client/**", router -> {
                String authorization = request.getHeader("Authorization");

                String uri = request.getRequestURI();
                if (uri.startsWith("/api/client")) {
                    if (!uri.endsWith("/api/client/register")) {
                        Long machineId = clientService.getIdByTokenWithCache(authorization);
                        if (Objects.isNull(machineId)) {
                            try {
                                response.setStatus(ErrorCode.UNAUTHORIZED.getCode());
                                response.getWriter().write(RestBean.failed(ErrorCode.UNAUTHORIZED).asJson());
                            } catch (IOException e) {
                                log.error("响应客户端失败", e);
                                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            }
                        } else {
                            request.setAttribute(RequestAttributeConst.CLIENT_MACHINE_ID, machineId);
                        }
                    }
                }
            });

            SaRouter.match("/**")
                    .notMatch("/api/client/**")
                    .notMatch("/api/v1/auth/login")  // 排除登录接口
                    .notMatch("/api/v1/auth/**")     // 排除所有认证相关接口
                    .notMatch("/api/gen/**")
                    .notMatch("/swagger/**")
                    .notMatch("/error")
                    .notMatchMethod("OPTIONS")       // 排除 OPTIONS 请求
                    .check(staff -> StpUtil.checkLogin());


        })).addPathPatterns("/**");
    }
}
