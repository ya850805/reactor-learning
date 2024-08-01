package com.learning.webflux.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.result.view.Rendering;

/**
 * @author jason
 * @description
 * @create 2024/7/31 21:02
 **/
@Controller
public class HelloController2 {
    // Rendering：一種視圖對象，新版的頁面跳轉API
    @GetMapping("/google")
    public Rendering render() {
        return Rendering.redirectTo("https://google.com").build();
    }
}
