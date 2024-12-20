package com.bixuebihui.test.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bixuebihui.test.business.TestGenManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransactionUsageServiceTest {
    @Autowired
    private TransactionUsageService transactionUsageService;

    @Autowired
    private TestGenManager testGenManager;

    @BeforeEach
    void setUp() {
        testGenManager.delete("where name like 'test%'", null, null);
    }

    @Test
    @Disabled //TODO: fix this test
    void oneTableRollback() {

        try{
            transactionUsageService.oneTableRollback();
        }catch (Exception e){
            System.out.println(e.getMessage());
            assertNull(testGenManager.selectByKey(Integer.parseInt(e.getMessage())));
        }

    }

    @Test
    void oneTableCommit() {
        try{
            transactionUsageService.oneTableCommit();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        assertNull(testGenManager.selectByKey(999));
    }

    @Test
    @Disabled //TODO: fix this test
    void oneTableLeftOver() {
        int id = transactionUsageService.oneTableLeftOver();
        assertNotNull(testGenManager.selectByKey(id));
    }
}
