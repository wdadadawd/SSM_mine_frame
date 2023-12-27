package com.lsu.spring.Annotation;

import java.lang.annotation.*;

/**
 * @author zt
 * @create 2023-12-24 20:21
 */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
    boolean required() default true;

    String value() default "";
}
