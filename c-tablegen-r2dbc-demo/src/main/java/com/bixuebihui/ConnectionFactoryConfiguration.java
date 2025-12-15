package com.bixuebihui;


import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactory;
import org.mariadb.r2dbc.MariadbConnectionConfiguration;
import org.mariadb.r2dbc.MariadbConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ConnectionFactoryConfiguration {
    @Bean("test")
    public ConnectionFactory connectionFactory() {
       // MySqlConnectionFactory connectionFactory = MySqlConnectionFactory.from(MySqlConnectionConfiguration.builder()
        MariadbConnectionConfiguration conf = MariadbConnectionConfiguration.builder()
                .host("127.0.0.1")         // 主机地址
                .port(3306)                // 端口
                .username("test")          // 用户名
                .password("test123")        // 密码
                .database("test")  // 连接的数据库名称
                .build();
        MariadbConnectionFactory connectionFactory = new MariadbConnectionFactory(conf);

        ConnectionPoolConfiguration configuration = ConnectionPoolConfiguration.builder(connectionFactory)
                .maxIdleTime(Duration.ofMillis(1000))
                .initialSize(10)
                .maxSize(100)
                .build();
        return new ConnectionPool(configuration);
        //return connectionFactory;
    }
}
