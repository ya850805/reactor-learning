package com.learning.flow;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

/**
 * @author jason
 * @description
 * @create 2024/7/16 22:19
 *
 *  發布訂閱模型：觀察者模式
 *      1. Publisher：發布者
 *      2. Subscriber：訂閱者
 *      3. Subscription：訂閱關係
 **/
public class FlowDemo {
    public static void main(String[] args) throws InterruptedException {
        // 1. 定義一個發布者，發布數據
        SubmissionPublisher<String> publisher = new SubmissionPublisher<>();

        // 1.5 定義一個中間操作，給每個元素加一個前綴
        MyProcessor myProcessor = new MyProcessor();

        // 2. 定義一個訂閱者，訂閱者感興趣發布者的數據
        Flow.Subscriber<String> subscriber = new Flow.Subscriber<String>() {  // onXxx：在xxx事件發生時，執行這個回調
            private Flow.Subscription subscription;

            // 在訂閱時
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                System.out.println(Thread.currentThread() + "訂閱開始了：" + subscription);
                this.subscription = subscription;

                // 從上游取得一個數據
                this.subscription.request(1);
            }

            // 在下一個元素到達時(接收到新數據)
            @Override
            public void onNext(String item) {
                System.out.println(Thread.currentThread() + "訂閱者接收到數據：" + item);

                // 背壓模式：按照自己消費數據的能力，再去取數據
                this.subscription.request(1);
                if ("p-7".equals(item)) {
                    this.subscription.cancel();  // 取消訂閱
                }
            }

            // 錯誤發生時
            @Override
            public void onError(Throwable throwable) {
                System.out.println(Thread.currentThread() + "訂閱者接收到錯誤：" + throwable);
            }

            // 在完成時
            @Override
            public void onComplete() {
                System.out.println(Thread.currentThread() + "訂閱者接收完成信號");
            }
        };

        // 3. 綁定發布者和訂閱者 -> 發布者有數據，訂閱者就會拿到
        // 綁定操作：就是發布者記住了所有訂閱者都有誰，有數據後給所有訂閱者把數據推送過去
//        publisher.subscribe(subscriber);
        publisher.subscribe(myProcessor);  // 此時處理器相當於訂閱者
        myProcessor.subscribe(subscriber);  // 此時處理器相當於發布者

        // 發布10條數據(給緩衝區存數據)
        for (int i = 0; i < 10; i++) {
            System.out.println(Thread.currentThread());
            publisher.submit("p-" + i); // publisher發布的所有數據在他的buffer區
        }

        publisher.close();
        Thread.sleep(2000);
    }
}

// 只需要實現訂閱者的方法(因為已經繼承了SubmissionPublisher)
class MyProcessor extends SubmissionPublisher<String> implements Flow.Processor<String, String> {

    private Flow.Subscription subscription;  // 保存綁定關係

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        System.out.println("processor訂閱綁定完成");
        this.subscription = subscription;
        this.subscription.request(1);  // 找上游要一個數據
    }

    @Override
    public void onNext(String item) {
        System.out.println("processor拿到數據" + item);
        //加工數據
        item += "~~~";
        submit(item);  // 把加工後的數據發出去
        this.subscription.request(1);  // 再要新的數據
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onComplete() {

    }
}
