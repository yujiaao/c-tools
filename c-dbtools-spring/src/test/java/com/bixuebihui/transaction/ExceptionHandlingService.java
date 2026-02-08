package com.bixuebihui.transaction;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataAccessException;

import java.sql.SQLException;

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
    public void methodWithNoRollbackFor() throws BusinessException {
        // 业务异常不会导致回滚
        throw new BusinessException("Business exception - no rollback");
    }
}