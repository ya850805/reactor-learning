package com.learning.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author jason
 * @description
 * @create 2024/8/15 19:22
 **/
@SpringBootTest
public class PasswordTest {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void test() {
        System.out.println(passwordEncoder.encode("123456"));
    }
}
