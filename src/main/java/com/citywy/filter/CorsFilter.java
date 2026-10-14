package com.citywy.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author：Ziyang Gao
 * @Date：2024/1/10 1:33
 */

@WebFilter("/*")
public class CorsFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 允许所有域进行跨域请求
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");

        // 允许的请求方法
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");

        // 允许的请求头字段
        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        // 允许使用凭据（例如 Cookie）
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");

        // 预检请求的缓存时间，单位：秒
        httpResponse.setHeader("Access-Control-Max-Age", "3600");

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}

