package com.learning.lambda;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author jason
 * @description
 * @create 2024/7/15 21:55
 *
 *  流特性：流是lazy的，不用終結操作就不會調用
 *
 *  流三大部分：
 *      1. 數據流
 *      2. N個中間操作
 *      3. 一個終止操作
 **/
public class StreamDemo {
    public static void main(String[] args) {
        List<Person> list = List.of(
            new Person("John", "男", 16),
            new Person("Marry", "女", 20),
            new Person("Tom", "男", 22),
            new Person("Wayne", "男", 33),
            new Person("Cindy", "女", 18)
        );

        Map<String, List<Person>> collect = list.stream()
                .filter(s -> s.getAge() > 15)
                .collect(Collectors.groupingBy(Person::getGender));

        System.out.println(collect);
    }


    public static void test2(String[] args) {
        List<Person> list = List.of(
            new Person("John", "男", 16),
            new Person("Marry", "女", 20),
            new Person("Tom", "男", 22),
            new Person("Wayne", "男", 33),
            new Person("Cindy", "女", 18)
        );

        // 拿到集合流就是拿到集合身拷貝的值，流的所有操作都是流的元素引用
        // 流裡面的每一個元素都必須走完整個流水線，才能輪到下一個元素
        list.stream()
                .filter(person -> person.getAge() > 18)  // 挑出>18的person(流)
                .peek(System.out::println)
                .map(Person::getName)  // 拿到所有人的姓名
                .peek(System.out::println)
                .flatMap(element -> Arrays.stream(element.split("")))
                .distinct()
                .limit(3)
                .sorted(String::compareTo)
                .forEach(System.out::println);

        System.out.println("============");

        List<Integer> collect = List.of(1, 2, 3, 4, 5, 6)
                .stream()
                .filter(i -> i > 2)  // 無條件遍歷流中的每一個元素
                .collect(Collectors.toList());
        System.out.println(collect);

        List<Integer> collect1 = List.of(1, 2, 3, 4, 5, 6)
                .stream()
                .takeWhile(i -> i > 2)  // 當滿足條件，拿到這個元素，不滿足直接結束流操作
                .collect(Collectors.toList());
        System.out.println(collect1);

    }

    public static void test1(String[] args) {
        // 挑出最大的偶數
        List<Integer> list = List.of(1, 2, 3, 4, 5, -2, 6, 7, 8, 9);

        // 1. for循環每個遍歷，找到偶數，下次找到的偶數和臨時變量比較，取最大的
        int max = 0;
        for (int i : list) {
            if (i % 2 == 0 && i > max) {
                max = i;
            }
        }
        System.out.println("最大偶數：" + max);

        // 2. Stream API
        // 2.1 把數據封裝成流，取得數據流，集合類.stream()
        // 2.2 定義流式操作
        // 2.3 獲取最終結果
        list.stream()
                .filter(element -> element % 2 == 0)  // 過濾出想要的值，如果斷言返回true就是要的數據，是一個中間操作
                .max(Integer::compare)  // 終止操作
                .ifPresent(System.out::println);

        // 1. 數據流
        // 1.1 手動創建流
        Stream<Integer> stream = Stream.of(1, 2, 3);
        Stream<Integer> concat = Stream.concat(Stream.of(2, 3, 4), stream);
        Stream.builder().add("11").add("22");

        // 1.2 從集合容器中獲取流
        List<Integer> integers = List.of(1, 2);
        Stream<Integer> stream1 = integers.stream();

        Set<Integer> integers1 = Set.of(1, 2);
        Stream<Integer> stream2 = integers1.stream();

        System.out.println("主線程：" + Thread.currentThread());

        // 流是并發還是不并發？和for有啥區別？  ===>  流也是用for循環每個處理
        // 默認不并發，也可以并發，并發以後要自己解決多線程安全問題
        // 有狀態數據將產生并發安全問題，流的所有操作都是無狀態，數據狀態僅在此函數內有效，不溢出至函數外
        long count = Stream.of(1, 2, 3, 4, 5)
                .parallel()  // 中間操作，并發流
                .filter(i -> {
                    System.out.println("filter線程：" + Thread.currentThread());
                    System.out.println("正在filter：" + i);
                    return i > 2;
                })
                .count();

        System.out.println("count：" + count);
    }
}

@NoArgsConstructor
@AllArgsConstructor
@Data
class Person {
    private String name;
    private String gender;
    private Integer age;
}
