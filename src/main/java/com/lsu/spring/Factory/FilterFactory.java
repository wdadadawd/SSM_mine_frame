package com.lsu.spring.Factory;

import javax.servlet.Filter;
import java.util.List;

/**
 * @author zt
 * @create 2023-12-22 21:11
 */
public interface FilterFactory {

    /**
     * 获取全部过滤器
     * @return
     */
    List<Filter> getFilters();

    /**
     * 获取自定义Filter
     * @param filterName
     * @return
     */
    Filter getFilters(String filterName);
}
