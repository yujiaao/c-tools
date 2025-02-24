package com.example.es.business;

import com.example.es.EsConfig;
import com.example.es.pojo.Contents;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest(classes = {
        ContentsManager.class,
        EsConfig.class
})
@EnableAutoConfiguration
class ContentsManagerTest {

    @Autowired
    EsConfig config;

    @Autowired
    ContentsManager chejiahaoContentsManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        //create index if not exists
        //chejiahaoContentsManager.createIndex();

    }



    @Test
    void testGetId() {
        Contents info = new Contents();
        info.setInfoId(1L);

        Long result = chejiahaoContentsManager.getId(info);
        Assertions.assertEquals(Long.valueOf(1), result);
    }

    @Test
    void testGetFieldAlias() {
        String result = ContentsManager.getFieldAlias("field");
        Assertions.assertEquals("field", result);
    }

    @Test
    void testGetAllFields() {
        List<String> result = chejiahaoContentsManager.getAllFields();
        Assertions.assertTrue(result.contains("info_id"));
    }

    @Test
    void testGetKeyName() {
        String result = chejiahaoContentsManager.getKeyName();
        Assertions.assertEquals("info_id", result);
    }

    @Test
    void testInsert() {
        boolean result = chejiahaoContentsManager.save(new Contents());
        Assertions.assertTrue(result);
    }

    @Test
    void testInsertBatch() {
        Contents info = new Contents();
        info.setInfoId(3L);
        info.setTitle("测试标题1---！");
        Contents info1 = new Contents();
        info1.setInfoId(4L);
        info1.setTitle("测试标题2---！");
        boolean result = chejiahaoContentsManager.saveBatch(List.of(info, info1));
        Assertions.assertEquals(true, result);
    }

    @Test
    void testGetIDs() {
        Contents info = new Contents();
        info.setInfoId(1L);
        List<Long> result = chejiahaoContentsManager.getIDs(List.of(info));
        Assertions.assertEquals(List.of(Long.valueOf(1)), result);
    }
}
