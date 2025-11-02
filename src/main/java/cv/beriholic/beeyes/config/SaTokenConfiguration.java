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
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfiguration implements WebMvcConfigurer {
    @Resource
    private HttpServletRequest request;
    @Resource
    private HttpServletResponse response;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        SaInterceptor saInterceptor = new SaInterceptor(handle -> {
            SaRouter.match("/**")
                    .notMatch("/api/v1/auth/login")  // 排除登录接口
                    .notMatch("/api/v1/auth/**")     // 排除所有认证相关接口
                    .notMatchMethod("OPTIONS")       // 排除 OPTIONS 请求
                    .check(r -> StpUtil.checkLogin());
        });

        registry.addInterceptor(saInterceptor)
                .addPathPatterns("/**")  // 拦截所有路径
                .excludePathPatterns(    // 明确排除不需要拦截的路径
                        "/api/gen/**",
                        "/swagger/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/api/v1/auth/**",  // 排除认证接口
                        "/error",
                        "/actuator/**"
                );
    }

}
