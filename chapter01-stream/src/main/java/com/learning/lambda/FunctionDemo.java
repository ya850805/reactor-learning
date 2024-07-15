package com.learning.lambda;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author jason
 * @description
 * @create 2024/7/14 23:43
 **/
public class FunctionDemo {
    public static void main(String[] args) {
        // 1. 定義數據提供者函數
        Supplier<String> supplier = () -> "42";
        System.out.println(supplier.get());

        // 2. 斷言：驗證是否是一個數字
        Predicate<String> isNumber = str -> str.matches("-?\\d+(\\.\\d+)?");
        System.out.println(isNumber.test("777"));
        System.out.println(isNumber.test("777a"));

        // 3. 轉換器：把字符串變成數字   類::方法
        Function<String, Integer> change = Integer::parseInt;

        // 4. 消費者：打印數字
        Consumer<Integer> consumer = integer -> {
            if (integer % 2 == 0) {
                System.out.println("偶數：" + integer);
            } else {
                System.out.println("奇數：" + integer);
            }
        };

        //把上述4個函數串在一起，判斷42這個字符串是奇數還是偶數
        myMethod(isNumber, supplier, consumer, change);

        myMethod(str -> str.matches("-?\\d+(\\.\\d+)?"),
                () -> "777",
                System.out::println,
                Integer::parseInt);
    }

    private static void myMethod(Predicate<String> isNumber, Supplier<String> supplier, Consumer<Integer> consumer, Function<String, Integer> change) {
        if (isNumber.test(supplier.get())) {
            consumer.accept(change.apply(supplier.get()));
        } else {
            System.out.println("非法的數字");
        }
    }
}
