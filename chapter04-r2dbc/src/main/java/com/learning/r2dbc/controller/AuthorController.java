package com.learning.r2dbc.controller;

import com.learning.r2dbc.entity.TAuthor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @author jason
 * @description
 * @create 2024/8/2 20:02
 *
 *  SpringBoot對r2dbc的自動配置
 *      1. R2dbcAutoConfiguration：主要配置連接工廠、連接池
 *      2. R2dbcDataAutoConfiguration：
 *          R2dbcEntityTemplate：操作數據庫的響應式客戶端，提供CRUD API
 *          數據類型映射關係、轉換器Converter，自定義R2dbcCustomConversions轉換器組件
 *      3. R2dbcRepositoriesAutoConfiguration：開啟Spring Data聲明式接口方式的CRUD
 *          類似mybatis-plus，提供了BaseMapper、IService，自帶CRUD功能
 *          Spring Data：提供了基礎的CRUD接口，不用寫任何實現的情況下，可以直接具有CRUD功能
 *      4. R2dbcTransactionManagerAutoConfiguration：事務管理
 **/
@RestController
public class AuthorController {

//    @GetMapping("/author")
//    public Flux<TAuthor> getAllAuthor() {
//
//    }
}
