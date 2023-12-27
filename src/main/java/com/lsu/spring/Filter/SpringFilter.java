package com.lsu.spring.Filter;

import com.lsu.spring.Factory.FilterFactory;
import com.lsu.spring.Utils.StringUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

import java.util.List;

/**
 * @author zt
 * @create 2023-12-24 16:02
 */
@WebFilter(urlPatterns = {"/*"})
public class SpringFilter implements Filter {

    private List<Filter> filterList;     //配置的过滤器集合

    private String webPath;             //静态资源存放路径

    private String staticPath;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext servletContext = filterConfig.getServletContext();
        //从上下文中获取过滤器工厂
        FilterFactory filterFactory = (FilterFactory) servletContext.getAttribute("filterFactory");
        if (filterFactory!=null)       //给过滤器集合属性赋值
            this.filterList = filterFactory.getFilters();
        staticPath = (String) servletContext.getAttribute("staticPath");//拼接静态文件存放的根目录
        webPath = servletContext.getRealPath("/").substring(0, servletContext.getRealPath("/").indexOf("\\target"));
        webPath = webPath + "\\src\\main\\webapp" + staticPath.replace("/","\\");
        for (Filter filter : this.filterList) {  // 初始化全部配置的过滤器
            filter.init(filterConfig);
        }
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        String path = req.getRequestURI().substring(req.getContextPath().length()+1);
        if (StringUtil.isEmpty(path))
            servletRequest.getRequestDispatcher("/page/" + path).forward(servletRequest, servletResponse);
        else {
            //合成静态文件路径
            String filePath = webPath + "\\" + path.replace("/","\\");
            File file = new File(filePath);
            if (file.exists()){        //判断文件是否存在
                servletRequest.getRequestDispatcher( staticPath + "/" + path).forward(servletRequest, servletResponse);
                //加上请求路径的前缀,交给默认的处理器处理
            }else {
                for (Filter filter : this.filterList) {                   //遍历过滤器
                    filter.doFilter(servletRequest,servletResponse,filterChain);  // 放行
                }
                //加上请求前缀,交给默认在处理器处理
                servletRequest.getRequestDispatcher("/page/" + path).forward(servletRequest, servletResponse);
            }
        }
    }

    @Override
    public void destroy() {
        for (Filter filter : this.filterList) {
            filter.destroy();
        }
    }
}
