package com.bixuebihui;

import dev.miku.r2dbc.mysql.MySqlConnectionConfiguration;
import dev.miku.r2dbc.mysql.MySqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConnectionFactoryConfiguration {
    @Bean("test")
    public ConnectionFactory connectionFactory() {
        return MySqlConnectionFactory.from(MySqlConnectionConfiguration.builder()
                .host("127.0.0.1")         // 主机地址
                .port(3306)                // 端口
                .username("test")          // 用户名
                .password("test123")        // 密码
                .database("test")  // 连接的数据库名称
                .build());
    }
}
