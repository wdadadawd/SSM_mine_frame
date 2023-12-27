package com.lsu.spring.enums;

/**
 * @author zt
 * @create 2023-12-25 14:06
 */
public enum RequestMethod {
    GET,
    HEAD,
    POST,
    PUT,
    PATCH,
    DELETE,
    OPTIONS,
    TRACE;

    private RequestMethod() {
    }
}
