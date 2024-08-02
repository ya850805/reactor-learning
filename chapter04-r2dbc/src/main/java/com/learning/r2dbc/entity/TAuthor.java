package com.learning.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jason
 * @description
 * @create 2024/8/1 20:15
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TAuthor {
    private Long id;
    private String name;
}
