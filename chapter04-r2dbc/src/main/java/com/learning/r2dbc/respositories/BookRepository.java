package com.learning.r2dbc.respositories;

import com.learning.r2dbc.entity.TBook;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * @author jason
 * @description
 * @create 2024/8/5 14:47
 **/
@Repository
public interface BookRepository extends R2dbcRepository<TBook, Long> {
    @Query("select b.*, t.name as name from t_book b left join t_author t on b.author_id = t.id where b.id = ?")
    Mono<TBook> findBookAndAuthor(Long bookId);
}
