package com.learning.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

/**
 * @author jason
 * @description
 * @create 2024/8/1 20:15
 **/
@Table("t_author")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TAuthor {
    @Id
    private Long id;
    private String name;

    // 1-N 如何封裝
    @Transient  // 臨時字段，並不是數據庫表中的一個字段
    private List<TBook> books;
}
