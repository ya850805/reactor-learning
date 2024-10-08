package com.learning.reactor;

import org.reactivestreams.Subscription;
import reactor.core.publisher.*;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
 *      1. doOnNext：每個數據(流的數據)到達的時候發
 *      2. doOnEach：每個元素(流的數據和信號)到達的時候觸發
 *      3. doOnRequest：消費者請求流元素的時候
 *      4. doOnError：流發生錯誤
 *      5. doOnSubscribe：流被訂閱的時候
 *      6. doOnTerminate：發送取消/異常信號中斷了流
 *      7. doOnCancel：流被取消
 *      8. doOnDiscard：流中元素被忽略的時候
 **/
public class FluxDemo {
    public static void main(String[] args) throws InterruptedException, IOException {
//        customSubscribe(args);
//        buffer(args);
//        limit(args);
//        generate(args);
//        create(args);
//        handle(args);
//        thread(args);
        thread1(args);

        System.in.read();
    }

    public static void thread1(String[] args) {
        Scheduler s = Schedulers.newParallel("parallel-scheduler", 4);

        final Flux<String> flux = Flux
                .range(1, 2)
                .map(i -> 10 + i)  // 只要不指定線程池，默認發布者用的線程就是訂閱者的線程
                .log()
                .publishOn(s)
                .map(i -> "value " + i)  // 這邊才是發布者指定的線程
                .log();

        new Thread(() -> flux.subscribe(System.out::println)).start();
    }

    public static void thread(String[] args) {
        // 響應式編程：全異步、消息、事件回調
        // 默認還是用當前線程(main)，生成整個流、發布流、流操作
        Flux.range(1, 10)
                .publishOn(Schedulers.boundedElastic())  // 在哪個線程池把這個流的數據和操作執行了，以下的所有操作都是用該線程池
                .log()
                .subscribe();

        // publishOn：改變發布者所在線程池
        // subscribeOn：改變訂閱者所在的線程池

        // 調度器：線程池
//        Schedulers.immediate();  // 無執行上下文，當前線程運行所有操作
//        Schedulers.single();  // 使用固定的一個單線程
//        Schedulers.boundedElastic();  // 有界、彈性調度，不是無限擴充的線程池，線程池中有10 * CPU核心個線程，隊列默認100k，keepAliveTime：60s
//        Schedulers.fromExecutor(new ThreadPoolExecutor(4, 8, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000)));  // 自定義線程池
//        Schedulers.parallel();  // 并發池
    }

    /**
     * handle()：自定義處理流中數據的規則
     * map()所有返回的類型類型必須一致，例如返回是一個Integer stream，但是handle()則可以自定義
     * @param args
     */
    public static void handle(String[] args) {
        Flux.range(1, 10)
                .handle((value, sink) -> {
                    System.out.println("拿到的值：" + value);
                    // 這邊可以做自定義操作，像是從數據庫依照value查到name等等的，之後把name通過sink發送出去
                    sink.next("張三" + value);  // sink就是可以向下發送數據的通道
                })
                .log()
                .subscribe();
    }

    public static void create(String[] args) throws InterruptedException {

        Flux.create(fluxSink -> {
            MyListener myListener = new MyListener(fluxSink);
            for (int i = 0; i < 100; i++) {
                myListener.online("張" + i);
            }
        })
        .log().subscribe();
    }

    /**
     * 編程方式創建序列(同步環境下使用generate)
     * Sink：接收器、水槽、通道
     * Source：數據源  Sink：接收端
     * @param args
     */
    public static void generate(String[] args) {
        // 0-10
        Flux<Object> flux = Flux.generate(() -> 0, // 初始值
                (state, sink) -> {
            if (state <= 10) {
                sink.next(state);  // 把元素傳出去
            } else {
                sink.complete();  // 完成信號
            }

            if (state == 7) {
                sink.error(new RuntimeException("遇到7，拋出錯誤..."));
            }

            return state + 1;  // 返回新的迭代state值
        });

        flux.log()
                .doOnError(throwable -> System.out.println("throwable = " + throwable))
                .subscribe();
    }

    public static void limit(String[] args) {
        Flux.range(1, 1000)
                .log()  // 限流觸發看上游是怎麼限流獲取數據的
                .limitRate(100)  // 第一次request(100)，以後request(75)
                .subscribe();

        // 75% 預取策略：limitRate(100)
        // 第一次抓取100個數據，如果75%的元素已經處理了，繼續抓取新的75%元素
    }

    public static void buffer(String[] args) {
        Flux<List<Integer>> flux = Flux.range(1, 10)
                .buffer(3);// 緩衝區，緩衝3個元素。消費者一次最多可以拿到3個元素，湊滿數批量發給消費者

//        flux.subscribe(v -> System.out.println("v的類型：" + v.getClass() + " v的值：" + v));

        // 消費者每次request(1)，拿到的是3個數據(同buffer大小)
        // request(N)：找發布者請求N次數據，總共能得到 (N * bufferSize) 個數據
        flux.subscribe(new BaseSubscriber<List<Integer>>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                System.out.println("綁定關係...");
                request(1);
            }

            @Override
            protected void hookOnNext(List<Integer> value) {
                System.out.println("元素：" + value);
            }
        });
    }

    /**
     * subscribe：訂閱流，沒訂閱之前流什麼也不做
     *      流的元素開始流動，發生數據變化
     *      響應式編程：數據流(Flux/Mono) + 變化傳播(操作)
     *
     * doOnXxx：發生這個事件的時候產生一個回調，通知你(不能改變)
     * onXxx：發生這個事件後執行一個動作，可以改變元素、信號。 ex. onErrorComplete
     * @param args
     */
    public static void customSubscribe(String[] args) {
        Flux<String> flux = Flux.range(1, 10)
                .map(i -> {
                    System.out.println("map..." + i);
                    if (i == 9) {
                        i = 10 / (9 - i);  // 數學運算異常
                    }
                    return "哈哈" + i;
                })
                .onErrorComplete();  // 流錯誤的時候，把錯誤吃掉轉為正常信號

//        flux.subscribe();  // 流被訂閱(默認訂閱者)
//        flux.subscribe(v -> System.out.println("v = " + v));  // 指定訂閱規則(自定義正常消費者，只消費正常元素)
//        flux.subscribe(
//            v -> System.out.println("v = " + v),  // 流元素消費
//            throwable -> System.out.println("throwable = " + throwable),  // 感知異常結束
//            () -> System.out.println("流結束了...")   // 感知正常結束
//        );

        System.out.println("---------------");

        // 流的生命週期鉤子可以傳播給訂閱者
        flux.subscribe(new BaseSubscriber<String>() {
            // 生命週期鉤子：訂閱關係綁定的時候觸發
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                // 流被訂閱的時候觸發
                System.out.println("綁定了..." + subscription);

                // 找發布者要數據
                request(1);  // 要一個數據，給上游傳入數據傳送一個信號
//                requestUnbounded();  // 要無限個數據
            }

            @Override
            protected void hookOnNext(String value) {
                System.out.println("數據到達：" + value);
                if ("哈哈5".equals(value)) {
                    cancel();  // 取消流
                }
                request(1);  // 繼續要一個數據
            }

            // hookOnComplete, hookOnError 二選一執行
            @Override
            protected void hookOnComplete() {
                System.out.println("流正常結束");
            }

            @Override
            protected void hookOnError(Throwable throwable) {
                System.out.println("流異常..." + throwable);
            }

            @Override
            protected void hookOnCancel() {
                System.out.println("流被取消...");
            }

            @Override
            protected void hookFinally(SignalType type) {
                System.out.println("最終回調，一定會執行");
            }
        });
    }

    public static void log(String[] args) {
        Flux.concat(Flux.just(1, 2, 3), Flux.just(7, 8, 9))
                .subscribe(System.out::println);

        // Flux、Mono、彈珠圖、事件感知API，每個操作都是操作的上個流的東西
        Flux.range(1, 7)
//                .log()  // 日誌寫在這邊會有1~7的日誌
                .filter(i -> i > 3)  // 挑出 > 3的元素
//                .log()  // 日誌寫在這邊只會有4~7的日誌
                .map(i -> "haha" + i)  // 帶上一個前綴
                .log()
                .subscribe(System.out::println);
    }

    public static void doOnXxx(String[] args) {
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

class MyListener {
    FluxSink<Object> sink;

    public MyListener(FluxSink<Object> sink) {
        this.sink = sink;
    }

    // 用戶登入，觸發online監聽
    public void online(String userName) {
        System.out.println("用戶登入了：" + userName);
        sink.next(userName); // 傳入用戶
    }
}
