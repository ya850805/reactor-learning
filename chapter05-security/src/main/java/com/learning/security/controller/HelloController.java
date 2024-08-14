package com.learning.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author jason
 * @description
 * @create 2024/8/14 11:59
 **/
@RestController
public class HelloController {
    @GetMapping("/hello")
    public Mono<String> hello() {
        return Mono.just("hello world!");
    }
}
