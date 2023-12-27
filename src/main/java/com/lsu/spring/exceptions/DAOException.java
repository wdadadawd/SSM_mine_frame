package com.lsu.spring.exceptions;

/**
 * @author zt
 * @create 2022-07-23 21:44
 */
public class DAOException extends RuntimeException {
        public DAOException(String msg){
            super(msg);
        }
}
