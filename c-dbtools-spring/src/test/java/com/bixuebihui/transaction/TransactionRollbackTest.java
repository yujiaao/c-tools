package com.bixuebihui.transaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 事务回滚：{@code SpringDataSources} + {@code DbHelper} 在 {@code @Transactional} 下随 Spring 默认规则回滚。
 */
@SpringBootTest(classes = TransactionConfig.class)
@DisplayName("事务回滚")
class TransactionRollbackTest {

    @Autowired
    private DbHelperTxService dbHelperTxService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("单条插入后 RuntimeException → users 表无该行")
    void rollbackAfterSingleInsert() {
        String u = "rb_one_" + System.nanoTime();
        assertThrows(RuntimeException.class, () ->
                dbHelperTxService.insertUserThenFail(u, u + "@rb.local"));
        assertEquals(0, countUsersByUsername(u));
    }

    @Test
    @DisplayName("同一事务内两条 DbHelper 插入后异常 → 两行均回滚")
    void rollbackAfterTwoDbHelperInserts() {
        String u1 = "rb_two_a_" + System.nanoTime();
        String u2 = "rb_two_b_" + System.nanoTime();
        assertThrows(RuntimeException.class, () ->
                dbHelperTxService.insertTwoUsersThenFail(u1, u1 + "@x", u2, u2 + "@x"));
        assertEquals(0, countUsersByUsername(u1));
        assertEquals(0, countUsersByUsername(u2));
    }

    @Test
    @DisplayName("DbHelper 写 users + JdbcTemplate 写 user_profile 后异常 → 两表均无残留")
    void rollbackMixedDbHelperAndJdbcTemplate() {
        String u = "rb_mix_" + System.nanoTime();
        assertThrows(RuntimeException.class, () ->
                dbHelperTxService.insertUserAndProfileThenFail(u, u + "@rb.local"));
        assertEquals(0, countUsersByUsername(u));
        Integer profiles = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_profile up "
                        + "INNER JOIN users usr ON usr.id = up.user_id WHERE usr.username = ?",
                Integer.class,
                u);
        assertNotNull(profiles);
        assertEquals(0, profiles.intValue());
    }

    private int countUsersByUsername(String username) {
        Integer n = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE username = ?",
                Integer.class,
                username);
        return n != null ? n : 0;
    }
}
