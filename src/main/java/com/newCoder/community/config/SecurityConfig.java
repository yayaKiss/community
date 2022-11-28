package com.newCoder.community.config;

import com.alibaba.fastjson.JSONObject;
import com.newCoder.community.constant.AuthorityConstant;
import com.newCoder.community.util.JsonResult;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author lijie
 * @date 2022-11-21 21:58
 * @Desc
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements AuthorityConstant {

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //授权
        http.authorizeRequests()
                .antMatchers(
                        "/user/setting.html","user/setting",
                        "/user/upload",
                        "/comment/add/**",
                        "/discuss/publish",
                        "/follow",
                        "/updateCode",
                        "/like",
                        "/message/**"
                )
                .hasAnyAuthority(
                        AUTHORITY_ADMIN,
                        AUTHORITY_MODERATOR,
                        AUTHORITY_USER
                )
                .antMatchers(
                        "/discuss/top","/discuss/wonderful"
                )
                .hasAnyAuthority(
                        AUTHORITY_MODERATOR
                )
                .antMatchers(
                        "/discuss/delete",
                        "/data/**","/actuator/**"
                )
                .hasAnyAuthority(
                        AUTHORITY_ADMIN
                )
                .anyRequest().permitAll()
                //关闭处理CSRF功能
                        .and().csrf().disable();

        //权限不足时处理
        http.exceptionHandling()
                //没有登录时处理
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        String xrequstedWith = request.getHeader("x-requested-with");
                        if("XMMLHttpRequest".equals(xrequstedWith)){
                            //说明是异步请求
                            PrintWriter writer = response.getWriter();
                            response.setContentType("application/json;utf-8");
                            writer.write(JSONObject.toJSONString(JsonResult.error(403,"您还没有登录")));
                        }else{
                            response.sendRedirect(request.getContextPath()+ "/login.html");
                        }
                    }
                })
                //登录了，但权限不够
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        String xrequstedWith = request.getHeader("x-requested-with");
                        if("XMLHttpRequest".equals(xrequstedWith)){
                            //说明是异步请求
                            response.setContentType("application/json;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(JSONObject.toJSONString(JsonResult.error(403,"您没有访问的权限")));
                        }else{
                            response.sendRedirect(request.getContextPath()+ "/denied");
                        }
                    }
                });


        //退出，security默认会拦截/logout路径,进行退出处理
        http.logout().logoutUrl("/sercuritylogout");
    }
}
