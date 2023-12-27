package com.lsu.spring.common;

import com.lsu.spring.enums.RequestMethod;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author zt
 * @create 2023-12-25 14:22
 */
public class RequestMethodBean {

    private String BeanName;        //控制器bean名

    private String[] requestUrl;      //请求地址

    private RequestMethod[] requestMethod;  //请求方式

    private String MethodName;       //控制器的方法名

    public RequestMethodBean(String beanName, String[] requestUrl, RequestMethod[] requestMethod, String methodName) {
        BeanName = beanName;
        this.requestUrl = requestUrl;
        this.requestMethod = requestMethod;
        MethodName = methodName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestMethodBean that = (RequestMethodBean) o;
        //如果请求路径包含当前请求的路径并且(请求方法为空或请求方法中包含该路径)则视为相等
        return Arrays.asList(requestUrl).contains(that.requestUrl[0]) &&
                (requestMethod.length == 0 || Arrays.asList(requestMethod).contains(that.requestMethod[0]));
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(requestUrl);
        result = 31 * result + Arrays.hashCode(requestMethod);
        return result;
    }

    public String getBeanName() {
        return BeanName;
    }

    public void setBeanName(String beanName) {
        BeanName = beanName;
    }

    public String[] getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String[] requestUrl) {
        this.requestUrl = requestUrl;
    }

    public RequestMethod[] getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(RequestMethod[] requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getMethodName() {
        return MethodName;
    }

    public void setMethodName(String methodName) {
        MethodName = methodName;
    }
}
