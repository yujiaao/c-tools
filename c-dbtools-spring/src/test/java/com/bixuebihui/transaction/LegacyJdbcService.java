package com.bixuebihui.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Service
public class LegacyJdbcService {
    
    @Autowired
    private DataSource dataSource;
    
    @Transactional
    public void executeWithTransaction() throws SQLException {
        // Spring会自动管理Connection
        Connection conn = dataSource.getConnection();
        try {
            // 检查当前是否在事务中
            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
            System.out.println("Transaction active: " + isActive);
            
            // 执行数据库操作
            String sql = "INSERT INTO users(username) VALUES(?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "testUser");
            ps.executeUpdate();
            
            // 自动提交由Spring管理
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }
}