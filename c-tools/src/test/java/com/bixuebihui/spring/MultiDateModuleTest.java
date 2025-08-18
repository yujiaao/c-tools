// 文件: c-tools/src/test/java/com/bixuebihui/spring/MultiDateModuleTest.java
package com.bixuebihui.spring;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class MultiDateModuleTest {

    static class TestBean {
        public Date date;
    }

    static class TestBeanWithTimestamp {
        public Timestamp date;
    }

    @Test
    void testMultiDateFormats() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new MultiDateModule());

        String[] testDates = {
                "{\"date\":\"2025-07-01 12:34:56\"}",
                "{\"date\":\"2025-07-01\"}",
                "{\"date\":\"2025-7-1\"}"
        };

        for (String json : testDates) {
            TestBean bean = mapper.readValue(json, TestBean.class);
            assertNotNull(bean.date, "Date 解析失败: " + json);

            TestBeanWithTimestamp bean2 = mapper.readValue(json, TestBeanWithTimestamp.class);
            assertNotNull(bean2.date, "Timestamp 解析失败: " + json);
        }
    }

    @Test
    void testInvalidDateFormat() {

        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new MultiDateModule());

        String json = "{\"date\":\"invalid-date\"}";
        assertThrows(Exception.class, () -> mapper.readValue(json, TestBean.class));
        assertThrows(Exception.class, () -> mapper.readValue(json, TestBeanWithTimestamp.class));
    }
}