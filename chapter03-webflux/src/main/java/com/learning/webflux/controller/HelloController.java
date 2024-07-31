package com.learning.webflux.controller;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * @author jason
 * @description
 * @create 2024/7/30 21:37
 *
 *  WebFlux向下兼容原來SpringMVC的大多數註解和API
 *  SpringMVC以前怎麼用，基本可以無縫切換
 *  底層：需要自己開始編寫響應式代碼
 **/
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "key", required = false, defaultValue = "哈哈") String key) {
        return "Hello World!!! key = " + key;
    }

    // 現在推薦的方式
    // 1. 返回單個數據Mono：Mono<Order>
    // 2. 返回多個數據Flux：Flux<Order>
    // 3. 配合Flux，完成SSE：Server Send Event 服務端事件推送

    @GetMapping("/haha")
    public Mono<String> haha() {
        return Mono.just("haha api");
    }

    @GetMapping("/hehe")
    public Flux<String> hehe() {
        return Flux.just("hehe1", "hehe2");
    }

    // text/event-stream
    // SSE 測試：服務端推送
    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> sse() {
        return Flux.range(1, 10)
                .map(i -> {
                    // 構建一個SSE對象
                    return ServerSentEvent.builder("ha" + i)
                            .id(i + "")
                            .comment("hei-" + i)
                            .event("haha")
                            .build();
                })
                .delayElements(Duration.ofMillis(500));
    }
}
