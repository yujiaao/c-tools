package com.bixuebihui.cache;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ObjectCacheTest  {

    ObjectCache oc;
    String key = "objtest";

    @BeforeEach
    protected void setUp() {
        System.out.println("Start...");

        oc = new ObjectCache();
    }


    @AfterEach
    public void tearDown() {
        oc.destroy();
        System.out.println("Done.");

    }


    @Test
    public void testBatchGet() throws SQLException {

        List<String> arr = new ArrayList<String>();
        arr.add("10101");
        arr.add("10102");
        Map<?, ?> m = oc.batchGet(key, arr);
        assertEquals(m.size(), 2);
        System.out.println(m);
    }


    public void testSelectById() throws SQLException {
        oc.selectById(key, "10101");
    }

    public void testUpdateByKey() throws SQLException {
        DictionaryItem info = (DictionaryItem) oc.selectById(key, "10101");
        String oldsort = info.getSort();
        info.setSort("123");
        assertTrue(oc.updateByKey(key, info.getId(), info));
        assertEquals("123", info.getSort());
        info.setSort(oldsort);
        assertTrue(oc.updateByKey(key, info.getId(), info));
        assertEquals(oldsort, info.getSort());
    }


    public void testInsertAutoNewKey() throws SQLException {
//		DictionaryItem info = (DictionaryItem) oc.selectById(key, "10101");
//
//		assertTrue(oc.insertAutoNewKey(key, info));
//		assertTrue(!"10101".equals(info.getMs_id()));
//		assertTrue(oc.deleteByKey(key,info.getMs_id()));
    }

}
