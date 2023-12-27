package com.lsu.test.Filter;

import com.lsu.spring.Annotation.MyWebFilter;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author zt
 * @create 2023-12-22 20:52
 */
@MyWebFilter(priority = 2)
public class Filter2 implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("第二个过滤器");
    }
}
