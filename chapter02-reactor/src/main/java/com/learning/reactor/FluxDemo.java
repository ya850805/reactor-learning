package com.learning.reactor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;

/**
 * @author jason
 * @description
 * @create 2024/7/17 22:49
 *
 *  Mono：0|1個元素的流
 *  Flux：N個元素的流
 **/
public class FluxDemo {
    public static void main(String[] args) {
        Mono<Integer> just = Mono.just(1);
        just.subscribe(System.out::println);
    }

    /**
     * 測試Flux
     * @param args
     * @throws IOException
     */
    public static void flux(String[] args) throws IOException {
        // 發布者發布數據流：源頭
        // 1. 多元素的流
        Flux<Integer> just = Flux.just(1, 2, 3, 4, 5);

        // 流只要不消費就沒用
        just.subscribe(e -> System.out.println("e1 = " + e));
        // 一個數據流可以有很多消費者
        just.subscribe(e -> System.out.println("e2 = " + e));

        // 對於每個消費者來說流都是一樣的：廣播模式

        System.out.println("=======================");

        Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));// 每秒產生一個從0開始的遞增數字
        interval.subscribe(System.out::println);

        System.in.read();  // 測試使用，因為流是異步的，控制台輸入一個字，整個主線程才會退出
    }

}
