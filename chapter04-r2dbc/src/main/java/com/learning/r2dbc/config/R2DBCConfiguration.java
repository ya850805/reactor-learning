package com.learning.r2dbc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

/**
 * @author jason
 * @description
 * @create 2024/8/4 20:56
 **/
@EnableR2dbcRepositories  // 開啟R2dbc repository支持
@Configuration
public class R2DBCConfiguration {
}
