package com.learning.security.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author jason
 * @description
 * @create 2024/8/15 01:34
 **/
@Component
public class AppReactiveUserDetailsService implements ReactiveUserDetailsService {

    @Autowired
    private DatabaseClient databaseClient;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 自定義按照用戶名去數據庫查詢用戶信息
    @Override
    public Mono<UserDetails> findByUsername(String username) {
        Mono<UserDetails> userDetailsMono = databaseClient.sql("select u.*, r.id rid, r.name, r.value, pm.id pid, pm.value pvalue, pm.description from t_user u " +
                        "left join t_user_role ur on ur.user_id = u.id " +
                        "left join t_roles r on ur.role_id = r.id " +
                        "left join t_role_perm rp on rp.role_id = r.id " +
                        "left join t_perm pm on rp.perm_id = pm.id " +
                        "where u.username = ? limit 1")
                .bind(0, username)
                .fetch()
                .one()  // 需要改成 .all() + .bufferUntilChanged()
                .map(map -> {
                    UserDetails userDetails = User.builder()
                            .username(username)
                            .password(map.get("password").toString())
//                            .passwordEncoder(passwordEncoder::encode)
//                            .authorities("download", "view", "delete")  //可以從map中取
                            .authorities(new SimpleGrantedAuthority("delete"))
                            .roles("admin", "sale", "haha")  //可以從map中取
                            .build();
                    return userDetails;
                });
        return userDetailsMono;
    }
}
