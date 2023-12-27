package com.lsu.spring.exceptions;

/**
 * @author zt
 * @create 2022-07-23 22:08
 */
public class DispatcherServletException extends RuntimeException{
    public DispatcherServletException(String msg){
        super(msg);
    }
}
