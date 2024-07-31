package com.learning.webflux;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServer;

import java.io.IOException;
import java.net.URI;

/**
 * @author jason
 * @description
 * @create 2024/7/29 20:43
 **/
public class FluxMainApplication {
    public static void test(String[] args) throws IOException {
        // 編寫一個能處理請求的服務器

        // 1. 創建一個能處理Http請求的處理器，參數：請求、響應，返回值：Mono<Void> 代表處理完成的信號
        HttpHandler httpHandler = (request, response) -> {
            // 編寫請求處理的業務
            URI uri = request.getURI();
            System.out.println(Thread.currentThread() + "接受到請求：" + uri);

            // 給瀏覽器寫一個一內容：URL + "Hello"

            // 創建響應數據的buffer
            DataBufferFactory dataBufferFactory = response.bufferFactory();
            // 數據Buffer
            DataBuffer buffer = dataBufferFactory.wrap((uri + "~~~~Hello").getBytes());

            // 需要一個DataBuffer的發布者，writeWith()方法也會返回Mono<Void>
            return response.writeWith(Mono.just(buffer));
        };

        // 2. 啟動一個服務器，監聽8080端口，接受數據，拿到數據交給 HttpHandler 進行請求處理
        ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);
        HttpServer.create()
                .host("localhost")
                .port(8080)
                .handle(adapter)  // 用指定的處理器處理請求
                .bindNow();  // 馬上綁定

        System.out.println("服務器啟動完成，監聽8080，接受請求");
        System.in.read();
        System.out.println("服務器停止...");
    }
}
