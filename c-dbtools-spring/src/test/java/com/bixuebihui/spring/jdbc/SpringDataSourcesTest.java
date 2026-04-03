package com.bixuebihui.spring.jdbc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SpringDataSources} 无容器时的最小行为验证。
 */
@DisplayName("SpringDataSources 工具类")
class SpringDataSourcesTest {

    @Test
    @DisplayName("forTransactions 返回 TransactionAwareDataSourceProxy")
    void forTransactionsWrapsWithSpringProxy() {
        DriverManagerDataSource raw = new DriverManagerDataSource();
        raw.setDriverClassName("org.h2.Driver");
        raw.setUrl("jdbc:h2:mem:spring_ds_unit;DB_CLOSE_DELAY=-1");
        raw.setUsername("sa");
        raw.setPassword("");

        DataSource wrapped = SpringDataSources.forTransactions(raw);

        assertInstanceOf(TransactionAwareDataSourceProxy.class, wrapped);
    }
}
