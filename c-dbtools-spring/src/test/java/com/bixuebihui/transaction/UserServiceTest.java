package com.bixuebihui.transaction;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TransactionConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {
    
    @Autowired
    private UserService userService;
    
    @Test
    @Order(1)
    @Transactional
    @DisplayName("测试默认事务配置 - 创建用户")
    public void testTransaction() {
        userService.createUserWithDefaultTx("test", "test@example.com");
    }
    
    @Test
    @Order(2)
    @Transactional
    @DisplayName("测试自定义事务配置 - 创建用户和用户资料")
    public void testCreateUserWithCustomTx() {
        Long userId = userService.createUserWithDefaultTx("customUser", "custom@example.com");
        User user = userService.getUserById(userId);
        assertNotNull(user, "用户不应该为空");
        userService.createUserProfile(user.getId());
    }
    
    @Test
    @Order(3)
    @Transactional
    @DisplayName("测试创建用户资料")
    public void testCreateUserProfile() {
        Long userId = userService.createUserWithDefaultTx("profileUser", "profile@example.com");
        User user = userService.getUserById(userId);
        assertNotNull(user, "用户不应该为空");
        userService.createUserProfile(user.getId());
    }
    
    @Test
    @Order(4)
    @Transactional
    @DisplayName("测试只读事务 - 查询用户")
    public void testGetUserById() {
        Long userId = userService.createUserWithDefaultTx("queryUser", "query@example.com");
        User user = userService.getUserById(userId);
        assertNotNull(user, "用户不应该为空");
        assertEquals("queryUser", user.getUsername(), "用户名应该匹配");
        assertEquals("query@example.com", user.getEmail(), "邮箱应该匹配");
    }
    
    @Test
    @Order(5)
    @Transactional
    @DisplayName("测试嵌套事务操作")
    public void testNestedTransactionOperation() {
        userService.nestedTransactionOperation();
    }
    
    @Test
    @Order(6)
    @Transactional
    @DisplayName("测试事务回滚 - RuntimeException")
    public void testTransactionRollbackOnRuntimeException() {
        Long userId = userService.createUserWithDefaultTx("rollbackUser", "rollback@example.com");
        User user = userService.getUserById(userId);
        assertNotNull(user, "用户应该创建成功");
        assertEquals("rollbackUser", user.getUsername());
        assertThrows(RuntimeException.class, () -> {
            userService.methodThatThrowsRuntimeException();
        });
    }
    
    @Test
    @Order(7)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @DisplayName("验证事务回滚后的数据状态")
    public void testVerifyRollbackDataState() {
        Long userId = userService.createUserWithDefaultTx("verifyUser", "verify@example.com");
        User user = userService.getUserById(userId);
        assertNotNull(user, "用户应该创建成功");
        assertEquals("verifyUser", user.getUsername());
        assertThrows(RuntimeException.class, () -> {
            userService.methodThatThrowsRuntimeException();
        });
        boolean exists = userService.userExists(userId);
        assertFalse(exists, "verifyUser 用户不应该存在");
    }
    
    @Test
    @Order(8)
    @Transactional
    @DisplayName("测试事务不回滚 - BusinessException")
    public void testTransactionNoRollbackOnBusinessException() {
        Long userId = userService.createUserWithDefaultTx("businessUser", "business@example.com");
        User user = userService.getUserById(userId);
        assertNotNull(user, "用户不应该为空");
        assertThrows(BusinessException.class, () -> {
            userService.methodThatThrowsBusinessException();
        });
        boolean exists = userService.userExists(userId);
        assertTrue(exists, "businessUser 用户应该存在");
    }
    
    @Test
    @Order(9)
    @Transactional
    @DisplayName("测试事务超时配置")
    public void testTransactionTimeout() {
        Long userId = userService.createUserWithDefaultTx("timeoutUser", "timeout@example.com");
        User user = userService.getUserById(userId);
        assertNotNull(user, "用户不应该为空");
        userService.createUserProfile(user.getId());
    }
}