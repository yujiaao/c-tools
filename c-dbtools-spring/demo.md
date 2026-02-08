要将现有的JDBC数据源整合到Spring事务管理中，实现注解方式的事务管理，可以按照以下步骤进行：

## 1. 添加Spring依赖（以Maven为例）

```xml
<!-- Spring核心 -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>5.3.23</version>
</dependency>

<!-- Spring事务 -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-tx</artifactId>
    <version>5.3.23</version>
</dependency>

<!-- Spring JDBC（可选，但推荐） -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
    <version>5.3.23</version>
</dependency>

<!-- AOP支持 -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aop</artifactId>
    <version>5.3.23</version>
</dependency>
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
    <version>1.9.19</version>
</dependency>
```

## 2. 配置Spring事务管理器

### 方式一：Java配置类（推荐）

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement  // 启用注解事务
public class TransactionConfig {
    
    // 假设你已经有一个DataSource
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
```

### 方式二：XML配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/tx
        http://www.springframework.org/schema/tx/spring-tx.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd">

    <!-- 启用注解驱动 -->
    <context:annotation-config/>
    <context:component-scan base-package="com.yourpackage"/>
    
    <!-- 配置事务管理器 -->
    <bean id="transactionManager" 
          class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"/>
    </bean>
    
    <!-- 启用事务注解 -->
    <tx:annotation-driven transaction-manager="transactionManager"/>
    
</beans>
```

## 3. 使用@Transactional注解管理事务

### 示例Service类

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.sql.DataSource;

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
    public void createUserWithDefaultTx(String username, String email) {
        jdbcTemplate.update("INSERT INTO users(username, email) VALUES(?, ?)", 
                           username, email);
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
}
```

## 4. 如果你需要保持原有的Connection获取方式

如果你不想改用JdbcTemplate，可以这样改造：

```java
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
    public void executeWithTransaction() {
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
```

## 5. 异常处理建议

```java
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataAccessException;

@Service
public class ExceptionHandlingService {
    
    /**
     * 默认情况下，Spring只回滚运行时异常（RuntimeException）和错误（Error）
     * 检查异常（checked exception）不会导致回滚
     */
    @Transactional
    public void methodWithRuntimeException() {
        // 发生运行时异常，事务回滚
        throw new RuntimeException("This will rollback");
    }
    
    @Transactional
    public void methodWithCheckedException() throws Exception {
        // 默认情况下，检查异常不会导致回滚
        throw new Exception("This will NOT rollback by default");
    }
    
    /**
     * 指定需要回滚的异常类型
     */
    @Transactional(rollbackFor = {Exception.class, SQLException.class})
    public void methodWithRollbackFor() throws Exception {
        // 现在检查异常也会导致回滚
        throw new Exception("This WILL rollback");
    }
    
    /**
     * 指定不需要回滚的异常类型
     */
    @Transactional(noRollbackFor = {BusinessException.class})
    public void methodWithNoRollbackFor() {
        // 业务异常不会导致回滚
        throw new BusinessException("Business exception - no rollback");
    }
}
```

## 6. 最佳实践建议

1. **在Service层使用@Transactional**，而不是在DAO层
2. **避免在private方法上使用@Transactional**（需要通过代理才生效）
3. **明确指定rollbackFor**以避免意外行为
4. **考虑事务传播行为**（Propagation）：
   - REQUIRED（默认）：支持当前事务，如果不存在则创建新事务
   - REQUIRES_NEW：总是创建新事务
   - NESTED：嵌套事务
   - SUPPORTS：支持当前事务，如果没有则以非事务方式执行

5. **测试事务**：
```java
@SpringBootTest
class UserServiceTest {
    
    @Autowired
    private UserService userService;
    
    @Test
    @Transactional  // 测试后自动回滚
    public void testTransaction() {
        userService.createUserWithDefaultTx("test", "test@example.com");
        // 测试完成后会自动回滚，不会污染数据库
    }
}
```

这样你就可以将现有的JDBC操作无缝整合到Spring事务管理中了。