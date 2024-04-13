package com.bixuebihui.test.service;

import com.bixuebihui.test.business.TestGenManager;
import com.bixuebihui.test.pojo.TestGen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



/**
 * 事务使用示例
 *
 * @since 2021-04-25
 */
@Transactional("txManager")
@Service
public class TransactionUsageService {
    @Autowired
    private TestGenManager testGenManager;


    public void oneTableRollback() {
        TestGen info = getTestGen();
        if(testGenManager.save(info)){
            throw new RuntimeException(info.getId()+"");
        }
    }

    public void oneTableCommit() {
        TestGen info = getTestGen();
        if(testGenManager.save(info)){
            testGenManager.deleteByKey(info.getId());
        }
    }

    public int oneTableLeftOver() {
        TestGen info = getTestGen();
        if(testGenManager.save(info)){
            System.out.println("test left over");
        }
        return info.getId();
    }

    private static TestGen getTestGen() {
        TestGen info = new TestGen();
        info.setEduId(1);
        info.setName("test left over");
        return info;
    }
}
