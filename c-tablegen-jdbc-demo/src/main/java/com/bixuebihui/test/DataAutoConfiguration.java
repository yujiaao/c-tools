package com.bixuebihui.test;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
*  数据源模板
*
*
 * @author xingwanxiang
 * @date '2021-04-25 14:47:12'
 */
@Configuration
@EnableTransactionManagement
public class DataAutoConfiguration {

    @Bean(name = "test")
    @ConfigurationProperties(prefix = "spring.datasource.test")
    public DataSource mysqlDataSource() {
        return DataSourceBuilder.create().build();
    }


    @Bean(name = "txManager")
    public DataSourceTransactionManager txManager() {
        return new DataSourceTransactionManager(mysqlDataSource());
    }

}
