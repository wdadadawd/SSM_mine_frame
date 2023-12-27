package com.lsu.test.Filter;

import com.lsu.spring.Annotation.MyWebFilter;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author zt
 * @create 2023-12-24 19:48
 */
@MyWebFilter
public class Filter3 implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("第三个过滤器");

    }
}
