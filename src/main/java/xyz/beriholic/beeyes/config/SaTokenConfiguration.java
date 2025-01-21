package xyz.beriholic.beeyes.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(_ -> {
            SaRouter.match("/**")
                    .notMatch("/api/auth/**", "/error")
                    .notMatch("/swagger-ui/**", "/v3/api-docs/**")
                    .notMatch("/client/*")
                    .check(_ -> StpUtil.checkLogin());
        })).addPathPatterns("/**");
    }
}
