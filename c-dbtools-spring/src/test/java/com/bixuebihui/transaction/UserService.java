package com.bixuebihui.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;

@Service
public class UserService {
    
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public UserService(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    /**
     * 默认事务配置
     * propagation: REQUIRED（默认）- 如果当前没有事务，就新建一个事务
     * rollbackFor: 默认回滚RuntimeException和Error
     */
    @Transactional
    public Long createUserWithDefaultTx(String username, String email) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO users(username, email) VALUES(?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, username);
            ps.setString(2, email);
            return ps;
        }, keyHolder);
        
        // 返回刚创建的用户ID
        return keyHolder.getKey().longValue();
        // 如果发生异常，事务会自动回滚
    }
    
    /**
     * 自定义事务配置
     */
    @Transactional(
        rollbackFor = {Exception.class},      // 指定哪些异常回滚
        noRollbackFor = {BusinessException.class}, // 指定哪些异常不回滚
        readOnly = false,                     // 读写事务
        timeout = 30,                         // 超时时间（秒）
        isolation = Isolation.DEFAULT,        // 隔离级别
        propagation = Propagation.REQUIRED    // 传播行为
    )
    public void createUserWithCustomTx(User user) {
        // 复杂业务逻辑
        jdbcTemplate.update("INSERT INTO users(username, email) VALUES(?, ?)", 
                           user.getUsername(), user.getEmail());
        
        // 调用其他方法也会在同一个事务中
        createUserProfile(user.getId());
    }
    
    @Transactional
    public void createUserProfile(Long userId) {
        jdbcTemplate.update("INSERT INTO user_profile(user_id) VALUES(?)", userId);
    }
    
    /**
     * 只读事务，优化查询性能
     */
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return jdbcTemplate.queryForObject(
            "SELECT * FROM users WHERE id = ?",
            new Object[]{id},
            (rs, rowNum) -> new User(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("email")
            )
        );
    }
    
    /**
     * 嵌套事务示例
     */
    @Transactional(propagation = Propagation.NESTED)
    public void nestedTransactionOperation() {
        // 嵌套事务：如果外部事务存在，在嵌套事务中执行
        jdbcTemplate.update("INSERT INTO audit_log(action) VALUES('nested operation')");
    }
    
    /**
     * 使用默认 REQUIRED，便于与外层 {@code @Transactional}（含 Spring 测试事务）同一连接内可见；
     * 需要“新开事务读已提交数据”时再单独用 REQUIRES_NEW。
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean userExists(Long userId) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE id = ?",
                Integer.class,
                userId
            );
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void methodThatThrowsRuntimeException() {
        throw new RuntimeException("模拟业务异常，触发回滚");
    }

    /**
     * 单事务内插入后抛运行时异常，整笔回滚（用于测试，勿用 REQUIRES_NEW 拆开）。
     */
    @Transactional
    public void createUserThenThrowRuntime(String username, String email) {
        jdbcTemplate.update("INSERT INTO users(username, email) VALUES(?, ?)", username, email);
        throw new RuntimeException("rollback entire transaction");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean userExistsByUsername(String username) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE username = ?",
                Integer.class,
                username);
        return count != null && count > 0;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void methodThatThrowsBusinessException() throws BusinessException {
        throw new BusinessException("业务异常，不触发回滚");
    }
}