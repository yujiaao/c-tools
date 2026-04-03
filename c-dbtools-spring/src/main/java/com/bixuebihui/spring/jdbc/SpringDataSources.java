package com.bixuebihui.spring.jdbc;

import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;

/**
 * 让 c-dbtools 的 {@link com.bixuebihui.jdbc.DbHelper} 与 Spring
 * {@code @Transactional} 使用同一 JDBC 连接：对原始 {@link DataSource} 包一层即可。
 * <p>
 * 典型用法：把本方法返回的 DataSource 注入 {@code DbHelper#setDataSource(DataSource)}，
 * 与 {@code JdbcTemplate}、{@code @Transactional} 共用同一数据源 Bean。
 */
public final class SpringDataSources {

    private SpringDataSources() {
    }

    /**
     * 包装数据源，使 {@link DataSource#getConnection()} 参与当前 Spring 事务
     *（与 {@code JdbcTemplate}、声明式事务一致）。
     *
     * @param target 连接池或嵌入式库等「真实」数据源，勿重复包装
     * @return 事务感知代理，可交给 {@code DbHelper} 使用
     */
    public static DataSource forTransactions(DataSource target) {
        return new TransactionAwareDataSourceProxy(target);
    }
}
