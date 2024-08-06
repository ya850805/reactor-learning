package com.learning.r2dbc.config;

import com.learning.r2dbc.converter.BookConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.MySqlDialect;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

/**
 * @author jason
 * @description
 * @create 2024/8/4 20:56
 **/
@EnableR2dbcRepositories  // 開啟R2dbc repository支持
@Configuration
public class R2DBCConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public R2dbcCustomConversions conversions() {
        // 把我們的轉換器加入進去
        return R2dbcCustomConversions.of(MySqlDialect.INSTANCE, new BookConverter());
    }
}
