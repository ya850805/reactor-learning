package com.learning.reactor;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.io.IOException;
import java.time.Duration;

/**
 * @author jason
 * @description
 * @create 2024/7/17 22:49
 *
 *  Mono：0|1個元素的流
 *  Flux：N個元素的流
 *
 *  響應式編成核心：看懂文檔的圖
 *      信號：正常/異常(取消)  --> SignalType類
 *          SUBSCRIBE：被訂閱
 *          REQUEST：請求了N個元素
 *          CANCEL：流被取消
 *          ON_SUBSCRIBE：在訂閱的時候
 *          ON_NEXT：在元素到達
 *          ON_ERROR：在流錯誤
 *          ON_COMPLETE：在流正常完成時
 *          AFTER_TERMINATE：中斷以後
 *          CURRENT_CONTEXT：當前上下文
 *          ON_CONTEXT：感知上下文
 *
 *  doOnXxx API觸發時機：
 *      1. doOnNext：每個數據(流的數據)到達的時候觸發
 *      2. doOnEach：每個元素(流的數據和信號)到達的時候觸發
 *      3. doOnRequest：消費者請求流元素的時候
 *      4. doOnError：流發生錯誤
 *      5. doOnSubscribe：流被訂閱的時候
 *      6. doOnTerminate：發送取消/異常信號中斷了流
 *      7. doOnCancel：流被取消
 *      8. doOnDiscard：流中元素被忽略的時候
 **/
public class FluxDemo {
    public static void main(String[] args) {
        // doOnNext：表示流中某個元素到達以後觸發一個回調
        // 重要：doOnXxx要感知某個流的事件，就寫在這個流的後面、新流的前面
        Flux.just(1, 2, 3, 4, 5, 6, 7, 0, 5, 6)
                .doOnNext(i -> System.out.println("元素到達1：" + i))  // 元素到達時候觸發
                .doOnEach(integerSignal -> {  // doOnEach封裝的更詳細
                    System.out.println("doOnEach..." + integerSignal);
                })
                .map(i -> 10 / i)
                .doOnError(throwable -> {
                    System.out.println("異常：" + throwable);
                })
                .doOnNext(i -> System.out.println("元素到達2：" + i))
                .subscribe(System.out::println);
    }

    public static void fluxDoOnXxx(String[] args) throws IOException, InterruptedException {
//        Mono<Integer> just = Mono.just(1);
//        just.subscribe(System.out::println);

        // 事件感知：當流發生什麼事的時候，觸發一個回調，系統調用提前定義好的Hook鉤子函數：doOnXxx
        // 鏈式API中，下面的操作符，操作的是上面的流
        Flux<Integer> flux = Flux.range(1, 7)
                .delayElements(Duration.ofSeconds(1))
                .doOnComplete(() -> {
                    System.out.println("流正常結束");
                })
                .doOnCancel(() -> {
                    System.out.println("流已被取消");
                })
                .doOnError(throwable -> {
                    System.out.println("流出錯了：" + throwable);
                })
                .doOnNext(integer -> {
                    System.out.println("doOnNext：" + integer);
                });

        flux.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                System.out.println("訂閱者和發布者綁定好了：" + subscription);
                request(1);  // 要一個元素
            }

            @Override
            protected void hookOnNext(Integer value) {
                System.out.println("元素到達：" + value);
                if (value < 5) {
                    request(1);  // 繼續要元素
                    if (value == 3) {
                        throw new RuntimeException("ERROR....");
                    }
                } else {
                    cancel();  // 取消訂閱
                }
            }

            @Override
            protected void hookOnComplete() {
                System.out.println("數據流結束");
            }

            @Override
            protected void hookOnError(Throwable throwable) {
                System.out.println("數據流異常");
            }

            @Override
            protected void hookOnCancel() {
                System.out.println("數據流被取消");
            }

            @Override
            protected void hookFinally(SignalType type) {
                // 無論正常、異常都會執行
                System.out.println("結束信號：" + type);
            }
        });
//        Thread.sleep(2000);

        System.in.read();
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
