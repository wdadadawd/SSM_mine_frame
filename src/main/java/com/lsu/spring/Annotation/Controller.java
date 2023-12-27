package com.lsu.spring.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zt
 * @create 2023-12-24 23:33
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {

    String beanName() default "";       //自定义bean名

    String value() default "";      //路径
}
