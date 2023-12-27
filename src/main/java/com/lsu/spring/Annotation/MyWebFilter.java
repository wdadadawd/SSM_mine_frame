package com.lsu.spring.Annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zt
 * @create 2023-12-22 20:06
 * 自定义过滤器注解
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MyWebFilter {

    String beanName() default "";

    int priority() default 0;   //过滤器优先级
}
