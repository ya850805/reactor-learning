package com.learning.r2dbc;

import com.learning.r2dbc.entity.TAuthor;
import io.asyncer.r2dbc.mysql.MySqlConnectionConfiguration;
import io.asyncer.r2dbc.mysql.MySqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Statement;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

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
 **/
public class R2DBCTest {
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
                    return new TAuthor(id, name);
                }))
                .subscribe(author -> System.out.println("author = " + author));

        // 背壓：不用返回所有東西，基於請求量返回

        System.in.read();
    }
}
