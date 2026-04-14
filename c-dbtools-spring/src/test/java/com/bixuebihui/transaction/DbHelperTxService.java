package com.bixuebihui.transaction;

import com.bixuebihui.jdbc.DbHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;

/**
 * 演示：在 {@code @Transactional} 内使用 c-dbtools {@link DbHelper}（数据源须为 {@link com.bixuebihui.spring.jdbc.SpringDataSources#forTransactions} 包装）。
 */
@Service
public class DbHelperTxService {

    private final DbHelper db = new DbHelper();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DbHelperTxService(DataSource dataSource) {
        db.setDataSource(dataSource);
    }

    @Transactional
    public void insertUser(String username, String email) {
        db.executeNoQuery("INSERT INTO users(username, email) VALUES(?, ?)", new Object[]{username, email});
    }

    @Transactional
    public void insertUserThenFail(String username, String email) {
        db.executeNoQuery("INSERT INTO users(username, email) VALUES(?, ?)", new Object[]{username, email});
        throw new RuntimeException("intentional rollback");
    }

    /**
     * 同一事务内连续两次 DbHelper 插入后抛错，两行均应随事务回滚。
     */
    @Transactional
    public void insertTwoUsersThenFail(String u1, String e1, String u2, String e2) {
        db.executeNoQuery("INSERT INTO users(username, email) VALUES(?, ?)", new Object[]{u1, e1});
        db.executeNoQuery("INSERT INTO users(username, email) VALUES(?, ?)", new Object[]{u2, e2});
        throw new RuntimeException("rollback both inserts");
    }

    /**
     * DbHelper 写 users、JdbcTemplate 写 user_profile 后抛错，两表均应回滚。
     */
    @Transactional
    public void insertUserAndProfileThenFail(String username, String email) {
        db.executeNoQuery("INSERT INTO users(username, email) VALUES(?, ?)", new Object[]{username, email});
        Long userId = jdbcTemplate.queryForObject(
                "SELECT id FROM users WHERE username = ?",
                Long.class,
                username);
        jdbcTemplate.update("INSERT INTO user_profile(user_id) VALUES (?)", userId);
        throw new RuntimeException("rollback user and profile");
    }

    /**
     * 演示：同一 Spring 事务内，先 {@link DbHelper} 写入，再用 {@link JdbcTemplate} 能立即读到（读己之所写）。
     */
    @Transactional
    public int insertWithDbHelperThenCountWithJdbcTemplate(String username, String email) {
        db.executeNoQuery("INSERT INTO users(username, email) VALUES(?, ?)", new Object[]{username, email});
        Integer n = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE username = ?",
                Integer.class,
                username);
        return n != null ? n : 0;
    }

    /** 演示：在事务方法内当前线程已绑定 Spring 事务。 */
    @Transactional
    public boolean isSpringTransactionActive() {
        return TransactionSynchronizationManager.isActualTransactionActive();
    }
}
