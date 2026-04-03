package com.bixuebihui.transaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 验证：经 {@link com.bixuebihui.spring.jdbc.SpringDataSources} 包装后，DbHelper 写入随 Spring 事务提交/回滚。
 */
@SpringBootTest(classes = TransactionConfig.class)
@DisplayName("DbHelper + Spring 事务")
class DbHelperTransactionalTest {

    @Autowired
    private DbHelperTxService dbHelperTxService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("正常提交后数据落库")
    void commitPersists() {
        String u = "dbhelper_ok_" + System.nanoTime();
        dbHelperTxService.insertUser(u, "ok@example.com");
        Integer n = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE username = ?",
                Integer.class,
                u);
        assertNotNull(n);
        assertEquals(1, n.intValue());
    }

    @Test
    @DisplayName("运行时异常导致整笔事务回滚，无残留行")
    void rollbackRemovesRow() {
        String u = "dbhelper_rb_" + System.nanoTime();
        assertThrows(RuntimeException.class, () ->
                dbHelperTxService.insertUserThenFail(u, "rb@example.com"));
        Integer n = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE username = ?",
                Integer.class,
                u);
        assertNotNull(n);
        assertEquals(0, n.intValue());
    }
}
