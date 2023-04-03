package com.bixuebihui.test;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
*  数据源模板
*
*
 * @author xingwanxiang
 * @date '2021-04-25 14:47:12'
 */
@Configuration
public class DataAutoConfiguration {

    @Bean(name = "test")
    @ConfigurationProperties(prefix = "spring.datasource.test")
    public DataSource mysqlDataSource() {
        return DataSourceBuilder.create().build();
    }
}
