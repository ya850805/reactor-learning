package com.learning.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

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
    private Long id;
    private String name;
}
