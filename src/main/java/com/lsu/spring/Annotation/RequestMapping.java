package com.lsu.spring.Annotation;

import com.lsu.spring.enums.RequestMethod;

import java.lang.annotation.*;

/**
 * @author zt
 * @create 2023-12-25 13:59
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {

    String[] value() default {};      //路径

    RequestMethod[] method() default {};
}
