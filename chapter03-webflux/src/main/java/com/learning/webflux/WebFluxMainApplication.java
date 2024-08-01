package com.learning.webflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

/**
 * @author jason
 * @description
 * @create 2024/7/30 21:36
 **/

//@EnableWebFlux // 開啟WebFlux自定義，禁用WebFlux的默認效果，完全自定義
@SpringBootApplication
public class WebFluxMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebFluxMainApplication.class, args);
    }
}
