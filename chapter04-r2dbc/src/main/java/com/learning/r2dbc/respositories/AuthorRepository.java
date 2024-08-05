package com.learning.r2dbc.respositories;

import com.learning.r2dbc.entity.TAuthor;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.Collection;

/**
 * @author jason
 * @description
 * @create 2024/8/4 20:57
 **/
@Repository
public interface AuthorRepository extends R2dbcRepository<TAuthor, Long> {  // 默認繼承一堆CRUD方法，類似mybatis-plus
    // where id in () and name like ?
    // 起好方法名稱，就會有對應的SQL
    // 僅限單表複雜條件查詢
    Flux<TAuthor> findAllByIdInAndNameLike(Collection<Long> id, String name);

    // 多表複雜查詢
    @Query("select * from t_author")  // 自定義query註解，指定SQL語句
    Flux<TAuthor> test();

}
