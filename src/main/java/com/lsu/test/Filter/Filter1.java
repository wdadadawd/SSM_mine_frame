package com.lsu.test.Filter;
import com.lsu.spring.Annotation.MyWebFilter;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author zt
 * @create 2023-12-22 20:39
 */
@MyWebFilter(priority = 1)
public class Filter1 implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("第一个过滤器");
    }
}
