package com.learning.security.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * @author jason
 * @description
 * @create 2024/8/14 12:12
 **/
@Configuration
public class AppSecurityConfiguration {
    @Autowired
    private ReactiveUserDetailsService appReactiveUserDetailsService;

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        // 1. 定義哪些請求需要認證，哪些不需要
        http.authorizeExchange(authorize -> {
            // 1.1 允許所有人訪問靜態資源
            authorize.matchers(PathRequest.toStaticResources().atCommonLocations()).permitAll();

            // 1.2 剩下的所有請求都需要認證
            authorize.anyExchange().authenticated();
        });

        // 2. 開啟默認的表單登入
        http.formLogin();

        // 3. 安全控制
        http.csrf(csrfSpec -> {
            csrfSpec.disable();
        });

        // 目前認證：用戶名是user，密碼是默認生成的(項目啟動會打印在控制台)
        // 期望：去數據庫查詢用戶、密碼

        // 4. 配置認證規則：如何去數據庫查詢到用戶，Spring Security底層使用ReactiveAuthenticationManager去查詢用戶信息
        // ReactiveAuthenticationManager有一個實現是UserDetailsRepositoryReactiveAuthenticationManager：用戶信息去數據庫中查
        // UserDetailsRepositoryReactiveAuthenticationManager需要傳入ReactiveUserDetailsService
        // ---> 所以我們只需要自己寫一個ReactiveUserDetailsService：響應式的用戶查詳情服務
        http.authenticationManager(new UserDetailsRepositoryReactiveAuthenticationManager(appReactiveUserDetailsService));

        // 構建出安全配置
        return http.build();
    }
}
