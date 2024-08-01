package com.learning.webflux.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author jason
 * @description
 * @create 2024/7/31 20:21
 *
 *  全局異常處理
 **/
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ArithmeticException.class)
    public String error(ArithmeticException exception) {
        System.out.println("發生了數學運算異常：" + exception);
        return "ArithmeticException...";
    }
}
