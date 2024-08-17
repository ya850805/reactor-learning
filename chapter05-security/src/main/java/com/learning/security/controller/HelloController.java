package com.learning.security.controller;

import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('admin')")
    @GetMapping("/hello")
    public Mono<String> hello() {
        return Mono.just("hello world!");
    }

    @PreAuthorize("hasRole('haha')")
    @GetMapping("/world")
    public Mono<String> world() {
        return Mono.just("world!");
    }
}
