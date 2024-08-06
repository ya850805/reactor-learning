package com.learning.r2dbc.converter;

import com.learning.r2dbc.entity.TAuthor;
import com.learning.r2dbc.entity.TBook;
import io.r2dbc.spi.Row;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.Instant;

/**
 * @author jason
 * @description
 * @create 2024/8/5 15:00
 *
 *  告訴Spring Data怎麼封裝對象
 **/
@ReadingConverter  // 讀取數據庫數據的時候，把row轉成TBook
public class BookConverter implements Converter<Row, TBook> {
    @Override
    public TBook convert(Row source) {
        // 自定義結果集的封裝
        TBook tBook = new TBook();
        tBook.setId(source.get("id", Long.class));
        tBook.setTitle(source.get("title", String.class));
        Long authorId = source.get("author_id", Long.class);
        tBook.setAuthorId(authorId);
        tBook.setPublishTime(source.get("publish_time", Instant.class));

        TAuthor tAuthor = new TAuthor();
        tAuthor.setId(authorId);
        tAuthor.setName(source.get("name", String.class));
        tBook.setAuthor(tAuthor);
        return tBook;
    }
}
