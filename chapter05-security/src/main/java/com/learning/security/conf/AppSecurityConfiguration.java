package com.learning.security.conf;

import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * @author jason
 * @description
 * @create 2024/8/14 12:12
 **/
@Configuration
public class AppSecurityConfiguration {
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

        // 構建出安全配置
        return http.build();
    }
}
