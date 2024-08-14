package com.learning.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author jason
 * @description
 * @create 2024/8/14 11:34
 *
 *  Spring Security 默認行為：所有請求都需要登入才能訪問
 *      1. SecurityAutoConfiguration
 *          導入SecurityFilterChain組件：默認所有請求都需要登入才可以訪問
 *          默認登入頁
 *      2. SecurityFilterAutoConfiguration
 *      3. ReactiveSecurityAutoConfiguration
 *          導入ServerHttpSecurityConfiguration配置：註解導入ServerHttpSecurityConfiguration
 *      4. MethodSecurityAspectJAutoProxyRegistrar
 **/
@SpringBootApplication
public class SecurityDemoMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecurityDemoMainApplication.class, args);
    }
}
