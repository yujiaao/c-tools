package com.bixuebihui.transaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 使用演示：{@code SpringDataSources.forTransactions} + {@code DbHelper} + {@code @Transactional} 的典型行为验证。
 */
@SpringBootTest(classes = TransactionConfig.class)
@DisplayName("c-dbtools-spring 集成演示")
class SpringDbToolsDemoTest {

    @Autowired
    private DbHelperTxService dbHelperTxService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("事务内 DbHelper 写入后，同一事务中的 JdbcTemplate 能立即查到")
    void demoReadYourWritesInOneTransaction() {
        String u = "demo_ryw_" + System.nanoTime();
        int count = dbHelperTxService.insertWithDbHelperThenCountWithJdbcTemplate(u, u + "@demo.local");
        assertEquals(1, count, "同一事务内应读到刚插入的行");
    }

    @Test
    @DisplayName("@Transactional 方法内应处于 Spring 管理的事务中")
    void demoSpringTransactionIsActive() {
        assertTrue(dbHelperTxService.isSpringTransactionActive(), "DbHelper 使用的连接应参与 Spring 事务");
    }

    @Test
    @DisplayName("提交后库中可查到（通过 JdbcTemplate 在新调用中验证）")
    void demoCommitVisibleAfterMethodReturns() {
        String u = "demo_commit_" + System.nanoTime();
        dbHelperTxService.insertUser(u, u + "@demo.local");
        Integer n = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE username = ?",
                Integer.class,
                u);
        assertNotNull(n);
        assertEquals(1, n.intValue());
    }
}
