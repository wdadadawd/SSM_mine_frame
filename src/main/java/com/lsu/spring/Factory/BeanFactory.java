package com.lsu.spring.Factory;

import com.lsu.spring.common.BeanMethod;

/**
 * @author zt
 * @create 2023-12-22 21:08
 */
public interface BeanFactory {

    Object getBean(String name);

    BeanMethod getRequestBeanAndMethod(String requestUrl,String requestMethod);
}
