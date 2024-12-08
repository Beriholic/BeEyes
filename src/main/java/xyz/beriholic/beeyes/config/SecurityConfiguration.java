package xyz.beriholic.beeyes.config;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import xyz.beriholic.beeyes.entity.RestBean;
import xyz.beriholic.beeyes.entity.dto.Account;
import xyz.beriholic.beeyes.entity.vo.response.AuthorizeVO;
import xyz.beriholic.beeyes.filter.JwtAuthenticationFilter;
import xyz.beriholic.beeyes.filter.RequestLogFilter;
import xyz.beriholic.beeyes.service.AccountService;
import xyz.beriholic.beeyes.utils.Const;
import xyz.beriholic.beeyes.utils.JwtUtils;

import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfiguration {

    @Resource
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Resource
    RequestLogFilter requestLogFilter;

    @Resource
    JwtUtils utils;

    @Resource
    AccountService service;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(conf -> conf
                        .requestMatchers("/api/auth/**", "/error").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/client/*").permitAll()
                        .anyRequest().hasAnyRole(Const.ROLE_DEFAULT)
                )
                .formLogin(conf -> conf
                        .loginProcessingUrl("/api/auth/login")
                        .failureHandler(this::handleProcess)
                        .successHandler(this::handleProcess)
                        .permitAll()
                )
                .logout(conf -> conf
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(this::onLogoutSuccess)
                )
                .exceptionHandling(conf -> conf
                        .accessDeniedHandler(this::handleProcess)
                        .authenticationEntryPoint(this::handleProcess)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(conf -> conf
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(requestLogFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, RequestLogFilter.class)
                .build();
    }

    /**
     * 将多种类型的Handler整合到同一个方法中，包含：
     * - 登录成功
     * - 登录失败
     * - 未登录拦截/无权限拦截
     *
     * @param request                   请求
     * @param response                  响应
     * @param exceptionOrAuthentication 异常或是验证实体
     * @throws IOException 可能的异常
     */
    private void handleProcess(
            HttpServletRequest request,
            HttpServletResponse response,
            Object exceptionOrAuthentication
    ) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();

        if (exceptionOrAuthentication instanceof AccessDeniedException exception) {
            writer.write(RestBean.onForbidden(exception.getMessage()).asJsonString());
        } else if (exceptionOrAuthentication instanceof Exception exception) {
            writer.write(RestBean.onUnauthorized(exception.getMessage()).asJsonString());
        } else if (exceptionOrAuthentication instanceof Authentication authentication) {
            User user = (User) authentication.getPrincipal();
            Account account = service.findAccountByNameOrEmail(user.getUsername());
            String jwt = utils.createJwt(user, account.getUsername(), account.getId());
            if (jwt == null) {
                writer.write(RestBean.onForbidden("登录验证频繁，请稍后再试").asJsonString());
            } else {
                AuthorizeVO vo = account.asViewObject(AuthorizeVO.class, o -> o.setToken(jwt));
                vo.setExpire(utils.expireTime());
                writer.write(RestBean.onOk(vo).asJsonString());
            }
        }
    }

    private void onLogoutSuccess(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        String authorization = request.getHeader("Authorization");
        if (utils.invalidateJwt(authorization)) {
            writer.write(RestBean.onOk("退出登录成功").asJsonString());
            return;
        }
        writer.write(RestBean.onFail(400, "退出登录失败").asJsonString());
    }
}
