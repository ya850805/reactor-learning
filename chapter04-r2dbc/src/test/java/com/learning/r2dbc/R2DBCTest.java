package com.learning.r2dbc;

import com.learning.r2dbc.entity.TAuthor;
import com.learning.r2dbc.respositories.AuthorRepository;
import com.learning.r2dbc.respositories.BookAuthorRepository;
import com.learning.r2dbc.respositories.BookRepository;
import io.asyncer.r2dbc.mysql.MySqlConnectionConfiguration;
import io.asyncer.r2dbc.mysql.MySqlConnectionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author jason
 * @description
 * @create 2024/8/1 19:57
 *
 *  r2dbc基於全異步、響應式、消息驅動
 *
 *  思想：
 *      1. 有了r2dbc，我們的應用可以在數據庫層面天然支持高并發、高吞吐量
 *      2. 並不能提升開發效率
 *
 *  最佳實踐：提升生產效率的做法
 *      1. Spring Data R2DBC 基礎的CRUD用R2dbcRepository提供好了
 *      2. 自定義複雜SQL(單表)可以使用@Query
 *      3. 多表查詢複雜結果集：DatabaseClient 自定義SQL及結果封裝
 **/
@SpringBootTest
public class R2DBCTest {
    @Autowired
    private R2dbcEntityTemplate r2dbcEntityTemplate;  // 連接查詢不好做

    @Autowired
    private DatabaseClient databaseClient;  // 貼近底層，join操作好做，複雜查詢好用

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookAuthorRepository bookAuthorRepository;  // 做複雜查詢的

    @Test
    void connection() throws IOException {
        // 0. MySql配置
        MySqlConnectionConfiguration database = MySqlConnectionConfiguration.builder()
                .username("root")
                .password("root")
                .host("localhost")
                .port(3306)
                .database("for-r2dbc")
                .build();

        // 1. 獲取連接工廠
        MySqlConnectionFactory connectionFactory = MySqlConnectionFactory.from(database);

        // 2. 獲取連接、發送sql
        Mono.from(connectionFactory.create())
                .flatMapMany(connection -> Flux.from(connection
                        .createStatement("select * from t_author where id = ?id and name = ?name")
                        .bind("id", 1L)  // 具名參數
                        .bind("name", "張三")
                        .execute())
                )
                .flatMap(result -> result.map(readable -> {
                    Long id = readable.get("id", Long.class);
                    String name = readable.get("name", String.class);
                    return new TAuthor(id, name, null);
                }))
                .subscribe(author -> System.out.println("author = " + author));

        // 背壓：不用返回所有東西，基於請求量返回

        System.in.read();
    }

    @Test
    void testR2DBCTemplate() throws IOException {
        // Query by Criteria：QBC

        // 1. 代表查詢條件 where id = 1 and name = 張三
        Criteria criteria = Criteria
                .empty()
                .and("id").is(1L)
                .and("name").is("張三");

        // 2. 封裝為Query對象
        Query query = Query.query(criteria);

        r2dbcEntityTemplate.select(query, TAuthor.class)
                .subscribe(tAuthor -> System.out.println("tAuthor = " + tAuthor));

        System.in.read();
    }

    @Test
    void testDatabaseClient() throws IOException {
        // 底層操作
        databaseClient.sql("select * from t_author where id = ?")
                .bind(0, 2L)
                .fetch() // 抓取數據
                .all()  // 返回所有
                .map(map -> {  // 一條紀錄會是一個map
                    System.out.println("map = " + map);
                    String id = map.get("id").toString();
                    String name = map.get("name").toString();
                    return new TAuthor(Long.parseLong(id), name, null);
                })
                .subscribe(tAuthor -> System.out.println("tAuthor = " + tAuthor));

        System.in.read();
    }

    @Test
    void testAuthorRepository() throws IOException {
        // 簡單查詢
//        authorRepository.findAll()
//                .subscribe(tAuthor -> System.out.println("tAuthor = " + tAuthor));

        // 複雜查詢：1. QBE API  2. 自定義方法  3. 自定義SQL
        // 方法起名
//        authorRepository.findAllByIdInAndNameLike(Arrays.asList(1L, 2L), "張%")
//                .subscribe(tAuthor -> System.out.println("tAuthor = " + tAuthor));


        // 自定義@Query註解
        authorRepository.test()
                .subscribe(tAuthor -> System.out.println("tAuthor = " + tAuthor));

        System.in.read();
    }

    @Test
    void testBookRepository() throws IOException {
//        bookRepository.findAll()
//                .subscribe(book -> System.out.println("book = " + book));

        // 自定義轉換器封裝 BookConverter
//        bookRepository.findBookAndAuthor(1L)
//                .subscribe(book -> System.out.println("book = " + book));

        // 有了自定義轉換器後，會對以前的CURD產生影響(因為一般單表查詢沒有連接其他表的字段)
        // 轉換器的工作時機：Spring Data發現方法簽名只要是返回TBook，就會利用自定義轉換器進行工作
        // 解決辦法：
        //      1. 寫新的vo、repository + 自定義類型轉換器：TBookAuthor
        //      2. converter多寫判斷，兼容更多表類型：source.getMetadata().contains("name")
        System.out.println(bookRepository.findById(1L).block());

        System.out.println("===");

        System.out.println(bookAuthorRepository.findBookAndAuthor(1L).block());

        System.in.read();
    }

    @Test
    void testAuthor() throws IOException {
        authorRepository.findById(1L)
                .subscribe(tAuthor -> System.out.println("tAuthor = " + tAuthor));

        System.in.read();
    }

    @Test
    void testBufferUntilChanged() throws IOException {
        Flux.just(1, 2, 3, 4 , 5, 6, 7, 8, 9)
                .bufferUntilChanged(i -> i % 4 == 0)  // 如果下一個判定值比起上一個發生了變化就開一個新buffer，如果沒有變化就保存到原本buffer中
                .subscribe(list -> System.out.println("list = " + list));

        System.in.read();
    }

    @Test
    void testOneToMany() {
//        databaseClient.sql("select a.id aid, a.name, b.* from t_author a left join t_book b on a.id = b.author_id order by aid")
//                .fetch()
//                .all();
    }
}
