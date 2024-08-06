package com.learning.r2dbc.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * @author jason
 * @description
 * @create 2024/8/5 14:45
 **/
@Table("t_book")
@Data
public class TBook {
    @Id
    private Long id;
    private String title;
    private Long authorId;
    private Instant publishTime;  // 響應式中日期的映射用Instant或LocalDate、LocalDateTime

    private TAuthor author;  // 每一本書有唯一作者，查的時候也一起查詢出來
}
