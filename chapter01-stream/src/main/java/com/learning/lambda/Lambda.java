package com.learning.lambda;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * @author jason
 * @description
 * @create 2024/7/13 22:09
 *
 *
 *  Lambda表達式：(參數表) -> {方法體}
 *  分辨出接口是否為函數式接口。函數式接口就可以lambda簡化
 **/
public class Lambda {
    public static void main(String[] args) {
        // 有入參，無出參(消費者)
        BiConsumer<String, String> consumer = (a, b) -> { // 能接收2個入參
            System.out.println("哈哈：" + a + "，呵呵：" + b);
        };
        consumer.accept("1", "2");

        // 有入參，有出參(多功能函數)
        Function<String, Integer> function = Integer::parseInt;
        System.out.println(function.apply("2"));
        BiFunction<String, Integer, Long> biFunction = (a, b) -> 8L;
        System.out.println(biFunction.apply("a", 1));


        // 無入參，無出參(普通函數)
        Runnable runnable = () -> System.out.println("aaa");
        new Thread(runnable).start();

        // 無入參，有出參(提供者)
        Supplier<String> supplier = () -> UUID.randomUUID().toString();
        System.out.println(supplier.get());

        // 斷言，返回boolean
        Predicate<Integer> even = (t) -> t % 2 == 0;
        System.out.println(even.test(6)); // 正向判斷
        System.out.println(even.negate().test(6));  // 反向判斷
    }

    public static void main2(String[] args) {
        var names = new ArrayList<String>();
        names.add("Alice");
        names.add("Bob");
        names.add("Charlie");
        names.add("David");

        //比較器
//        Collections.sort(names, new Comparator<String>() {
//            @Override
//            public int compare(String o1, String o2) {
//                return o2.compareTo(o1);
//            }
//        });

        //類::方法  -> 引用類中的方法
//        Collections.sort(names, String::compareTo);
        Collections.sort(names, Comparator.reverseOrder());
        System.out.println(names);

        new Thread(() -> {
            System.out.println("哈哈");
        }).start();

        //最佳實踐：以後調用某個方法傳入參數，這個參數實例是一個接口對象，且只定義了一個方法，就直接用lambda簡化即可
    }

    /**
     * lambda簡化函數式接口實例創建
     * @param args
     */
    public static void main1(String[] args) {
        //1. 自己寫實現類
        MyInterface myInterface = new MyInterfaceImpl();
        System.out.println(myInterface.sum(1, 2));

        //2. 創建匿名實現類
        MyInterface myInterface1 = new MyInterface() {
            @Override
            public int sum(int i, int j) {
                return (i * i) + (j * j);
            }
        };
        System.out.println(myInterface1.sum(2, 3));

        //3. lambda表達式  參數列表 + 箭頭 + 方法體
        MyInterface myInterface2 = (x, y) -> {
            return (x * x) + (y * y);
        };
        System.out.println(myInterface2.sum(2, 3));

        MyHaha myHaha = () -> {
            return 1;
        };

        MyHehe myHehe = y -> {
            return y * y;
        };

        MyHehe hehe2 = y -> y + 1;
        System.out.println(hehe2.hehe(7));

        /**
         * 簡化寫法
         * 1) 參數類型可以不寫，只寫參數名，參數變量名可以隨意定義。參數表最少可以只有一個()，或者只有一個參數名
         * 2) 方法體如果只有一句話，{}可以省略
         **/
    }
}

//函數式接口：只要是函數式接口就可以用Lambda表達式簡化
//函數式街口：接口中有且只有一個未實現的方法，這個接口就叫函數式接口

interface MyInterface {
    int sum(int i, int j);
}

class MyInterfaceImpl implements MyInterface {
    @Override
    public int sum(int i, int j) {
        return i + j;
    }
}

interface MyHaha {
    int haha();
    default int heihei() {
        return 2;
    }
}

@FunctionalInterface //檢查註解，幫我們快速檢查我們寫的接口是否為函數式接口
interface MyHehe {
    int hehe(int i);
}
