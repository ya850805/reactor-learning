package com.learning;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.Context;

import java.io.IOException;
import java.io.Serializable;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jason
 * @description
 * @create 2024/7/26 23:54
 *
 *  filter、flatMap、concatMap、flatMapMany、transform、defaultIfEmpty、switchIfEmpty、concat、concatWith、merge、mergeWith、mergeSequential、zip、zipWith...
 *
 *  onSubscribe：流被訂閱
 *  request(unbounded)：請求無限數據
 *  onNext(2)：每個數據到達
 *  onNext(4)：每個數據到達
 **/
public class APITest {
    /**
     *  onSubscribe：流被訂閱
     *  request(unbounded)：請求無限數據
     *  onNext(2)：每個數據到達
     *  onNext(4)：每個數據到達
     *  onComplete()：流結束
     */
    @Test
    void filter() {
        Flux.just(1, 2, 3, 4)  // 流發布者
                .log()  // 1,2,3,4
                .filter(i -> i % 2 == 0)  // 過濾偶數，消費上面的流
//                .log()  // 2,4
                .subscribe();  // 最終消費者
    }

    @Test
    void flatMap() {  // 扁平化
        Flux.just("zhang san", "li si")
                .flatMap(v -> {  // 兩個人的名字，按照空格拆分，打印出所有的姓與名
                    String[] s = v.split(" ");
                    return Flux.fromArray(s);  // 把數組包裝成多元素流
                })
                .log()
                .subscribe();
    }

    /**
     *  concatMap：連接映射，一個元素可以變很多、單個，對於元素類型無限制
     *  concat：連接操作
     *  concatWith：連接的流和老流中的元素類型要一致
     */
    @Test
    void concatMap() {  //
        Flux.just(1, 2)
                .concatMap(s -> {
                    return Flux.just(s + "->a", 1);
                })
                .log()
                .subscribe();

        System.out.println("===");

        Flux.concat(Flux.just(1, 2), Flux.just("h", "i"), Flux.just("haha", "hehe"))
                .log()
                .subscribe();

        System.out.println("===");

        Flux.just(1, 2)
                .concatWith(Flux.just(4, 5, 6))
                .log()
                .subscribe();
    }

    /**
     * transform：把流變形成新數據，不會共享外部變量的值
     * transformDeferred：把流變形成新數據，會共享外部變量的值
     */
    @Test
    void transform() {
        AtomicInteger atomic = new AtomicInteger(0);
        Flux<String> flux = Flux.just("a", "b", "c")
//                .transform(values -> {
                .transformDeferred(values -> {
                    if (atomic.incrementAndGet() == 1) {
                        // 如果是第一次調用，老流中的所有元素轉成大寫
                        return values.map(String::toUpperCase);
                    } else {
                        return values;
                    }
                });

        // transform(無defer)，不會共享外部變量的值，無狀態轉換，無論多少個訂閱者，transform只執行一次
        // transformDeferred(有defer)，會共享外部變量的值，有狀態轉換，無論多少個訂閱者，每個訂閱者transform都執行一次
        flux.subscribe(v -> System.out.println("訂閱者1：v = " + v));
        flux.subscribe(v -> System.out.println("訂閱者2：v = " + v));
    }

    /**
     * defaultIfEmpty：指定默認值，兜底數據
     * switchIfEmpty：空轉換，調用動態兜底方法
     */
    @Test
    void empty() {
        // Mono.just(null)：流裡面有一個null值元素
        // Mono.empty()：流裡面沒有元素，只有完成信號/結束信號
        haha()
            .defaultIfEmpty("x")  // 如果發布者元素為null，指定默認值，否則用發布者的值
            .subscribe(v -> System.out.println("v = " + v));

        haha()
            .switchIfEmpty(hehe())
            .subscribe(v -> System.out.println("v = " + v));
    }
    Mono<String> haha() {
//        return Mono.just("a");
        return Mono.empty();
    }
    Mono<String> hehe() {
        return Mono.just("兜底數據...");
    }

    /**
     * concat：連接，A流所有元素和B流所有元素拼接
     * merge：合併，A流所有元素和B流所有元素按照時間序列合併
     * mergeSequential：按照哪個流先發元素排隊
     * @throws IOException
     */
    @Test
    void merge() throws IOException {
        // 按照時間合併
        Flux.merge(
            Flux.just(1, 2, 3).delayElements(Duration.ofSeconds(1)),
            Flux.just("a", "b").delayElements(Duration.ofMillis(1500)),
            Flux.just("haha", "hehe", "heihei", "xixi").delayElements(Duration.ofMillis(500))
        )
        .log()
        .subscribe();

        System.in.read();
    }

    /**
     * zip：無法成對的元素會被忽略，最多支持8流壓縮
     */
    @Test
    void zip() {
        // Tuple元組
        Flux.just(1, 2, 3)
                .zipWith(Flux.just("a", "b", "c", "d"))
                .map(tuple -> {
                    Integer t1 = tuple.getT1();  // 元組中的第一個元素
                    String t2 = tuple.getT2();  // 元組中的第二個元素
                    return t1 + "===>" + t2;
                })
                .log()
                .subscribe(v -> System.out.println("v = " + v));

        System.out.println("===");

        Flux.zip(Flux.just(1, 2), Flux.just(1, 2), Flux.just(2, 3), Flux.just(1))
                .map(tuple -> {
                    return tuple.getT1() + "~~" + tuple.getT2() + "~~" + tuple.getT3() + "~~" + tuple.getT4();
                })
                .log()
                .subscribe(v -> System.out.println("v = " + v));
    }

    /**
     * subscribe：消費者可以感知正常元素(try)與流發生錯誤的(catch)
     */
    @Test
    void error() {
        System.out.println("onErrorReturn：返回一個兜底默認值");
        Flux.just(1, 2, 0, 4)
                .map(i -> "100 / " + i + " = " + (100 / i))
                .onErrorReturn(ArithmeticException.class, "ArithmeticException：Divide by zero...(錯誤發生時的默認返回)")
                .subscribe(v -> System.out.println("v = " + v),
                        err -> System.out.println("err = " + err),
                        () -> System.out.println("流結束"));

        System.out.println("=====");

        System.out.println("onErrorResume：返回一個兜底方法");
        Flux.just(1, 2, 0, 4)
                .map(i -> "100 / " + i + " = " + (100 / i))
                .onErrorResume(error -> Mono.just("哈哈777"))
                .subscribe(v -> System.out.println("v = " + v),
                        err -> System.out.println("err = " + err),
                        () -> System.out.println("流結束"));

        System.out.println("=====");

        System.out.println("根據錯誤返回新值");
        Flux.just(1, 2, 0, 4)
                .map(i -> "100 / " + i + " = " + (100 / i))
                .onErrorResume(this::errorResume)
                .subscribe(v -> System.out.println("v = " + v),
                        err -> System.out.println("err = " + err),
                        () -> System.out.println("流結束"));

        System.out.println("=====");

        System.out.println("onErrorMap：捕獲並包裝一個異常");
        Flux.just(1, 2, 0, 4)
                .map(i -> "100 / " + i + " = " + (100 / i))
                .onErrorMap(err -> new BusinessException(err.getMessage() + "(自定義異常...)"))
                .subscribe(v -> System.out.println("v = " + v),
                        err -> System.out.println("err = " + err),
                        () -> System.out.println("流結束"));

        System.out.println("=====");

        System.out.println("doOnError：不吃掉異常，只在異常發生時做事");
        Flux.just(1, 2, 0, 4)
                .map(i -> "100 / " + i + " = " + (100 / i))
                .doOnError(err -> {
                    System.out.println("error已被紀錄 = " + err);
                })
                .subscribe(v -> System.out.println("v = " + v),
                        err -> System.out.println("err = " + err),
                        () -> System.out.println("流結束"));

        System.out.println("=====");

        System.out.println("doFinally：正常或錯誤都會觸發");
        Flux.just(1, 2, 3, 4)
                .map(i -> "100 / " + i + " = " + (100 / i))
//                .doOnError(err -> {
//                    System.out.println("error已被紀錄 = " + err);
//                })
                .doFinally(signalType -> System.out.println("流信號：" + signalType))
                .subscribe(v -> System.out.println("v = " + v),
                        err -> System.out.println("err = " + err),
                        () -> System.out.println("流結束"));

        System.out.println("=====");

        System.out.println("onErrorContinue：忽略當前異常，僅通知紀錄，繼續推進");
        Flux.just(1, 2, 3, 0, 5)
                .map(i -> 10 / i)
                .onErrorContinue((error, value) -> {
                    System.out.println("err = " + error);
                    System.out.println("value = " + value);
                    System.out.println("發現" + value + "有問題，繼續執行其他的");
                })  // 發生錯誤繼續
                .subscribe(v -> System.out.println("v = " + v),
                        err -> System.out.println("err = " + err),
                        () -> System.out.println("流結束"));

        System.out.println("=====");

        System.out.println("onErrorStop & onErrorComplete");
        Flux.just(1, 2, 3, 0, 5)
                .map(i -> 10 / i)
                .onErrorStop()
//                .onErrorComplete()  // 把錯誤結束信號，替換為正常結束信號
                .subscribe(v -> System.out.println("v = " + v),
                        err -> System.out.println("err = " + err),
                        () -> System.out.println("流結束"));

    }
    Mono<String> errorResume(Throwable throwable) {
        if (throwable instanceof NullPointerException) {
            return Mono.just("錯誤：空指針異常");
        }
        return Mono.just("錯誤：" + throwable.getMessage());
    }

    @Test
    void retryAndTimeout() throws IOException {
        Flux.just(1)
                .delayElements(Duration.ofSeconds(3))
                .log()
                .timeout(Duration.ofSeconds(2))
                .retry(2)  // 把流從頭到尾重新請求一次
                .onErrorReturn(2)
                .map(i -> i + "哈哈")
//                .log()
                .subscribe(v -> System.out.println("v = " + v));

        System.in.read();
    }

    @Test
    void sinks() throws IOException, InterruptedException {
//        Sinks.many();  // 發送Flux數據
//        Sinks.one();  // 發送Mono數據

        // Sinks：接受器，數據管道，所有數據順著這個管道往下走的

//        Sinks.many().unicast(); // 單波，這個管道只能綁定單個消費者
//        Sinks.many().multicast(); // 多波，這個管道能綁定多個消費者
//        Sinks.many().replay(); // 重放，這個管道能重放元素，是否給後來的訂閱者把之前的元素依然發給他
        // 從頭消費還是從訂閱的那一刻消費

//        Sinks.Many<Object> many = Sinks.many()
//                .multicast()
//                .onBackpressureBuffer();  // 背壓隊列

        // 發布者數據重放
        Sinks.Many<Object> many = Sinks.many().replay().limit(3);

        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                many.tryEmitNext("a-" + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        many.asFlux().subscribe(v -> System.out.println("v1 = " + v));
        Thread.sleep(1000);
        many.asFlux().subscribe(v -> System.out.println("v2 = " + v));  // 默認訂閱者從訂閱的那一刻開始接受元素，重放模式需要使用.replay()

        System.in.read();
    }

    @Test
    void cache() throws IOException {
        Flux<Integer> cache = Flux.range(1, 10)
                .delayElements(Duration.ofSeconds(1))
                .cache(1); // 緩存元素

        cache.subscribe();

        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            cache.subscribe(v -> System.out.println("v = " + v));
        });

        System.in.read();
    }

    @Test
    void block() {
        Integer last = Flux.just(1, 2, 4)
                .map(i -> i + 10)
                .blockLast();
        System.out.println(last);

        List<Integer> integers = Flux.just(1, 2, 4)
                .map(i -> i + 10)
                .collectList()
                .block();  // block也是一種訂閱者
        System.out.println(integers);
    }

    @Test
    void parallelFlux() {
        // 100個數據，8個線程，分批處理
        Flux.range(1, 100)
                .buffer(10)
                .parallel(8)
                .runOn(Schedulers.newParallel("yy"))  // 中間操作並行
                .log()
                .flatMap(Flux::fromIterable)
                .collectSortedList(Integer::compare)
                .subscribe(v -> System.out.println("v = " + v));
    }

    @Test
    void threadlocal() {
        // ThreadLocal在響應式編程中無法使用
        // 響應式中，數據流期間共享數據，Context API：Context：讀寫ContextView：只讀
        Flux.just(1, 2, 3)
                .transformDeferredContextual((flux, context) -> {
                    System.out.println("flux = " + flux);
                    System.out.println("context = " + context);
                    return flux.map(i -> i + "===>" + context.get("prefix"));
                })  // 支持context的中間操作
                .contextWrite(Context.of("prefix", "哈哈"))  // ThreadLocal共享了數據，上游的所有人能看到，Context由下游傳播給上游
                .subscribe(v -> System.out.println("v = " + v));
    }
}

class BusinessException extends RuntimeException {
    public BusinessException(String msg) {
        super(msg);
    }
}
