package com.learning.security.component;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author jason
 * @description
 * @create 2024/8/15 01:34
 **/
@Component
public class AppReactiveUserDetailsService implements ReactiveUserDetailsService {

    // 自定義按照用戶名去數據庫查詢用戶信息
    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return null;
    }
}
