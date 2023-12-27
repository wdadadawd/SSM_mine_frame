package com.lsu.spring.common;

/**
 * @author zt
 * @create 2023-12-25 15:05
 */
public class BeanMethod {

    private Object bean;

    private String methodName;

    public BeanMethod(Object bean, String methodName) {
        this.bean = bean;
        this.methodName = methodName;
    }

    @Override
    public String toString() {
        return "BeanMethod{" +
                "bean=" + bean +
                ", methodName='" + methodName + '\'' +
                '}';
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
