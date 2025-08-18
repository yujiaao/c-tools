// 文件: src/test/java/com/bixuebihui/spring/MultiDateModuleTest.java
package com.bixuebihui.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 MultiDateModule 的多日期格式解析功能
 */
class MultiDateModuleTest {

    static class TestBean {
        public Date date;
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
            assertNotNull(bean.date, "日期解析失败: " + json);
        }
    }

    @Test
    void testInvalidDateFormat() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new MultiDateModule());

        String json = "{\"date\":\"invalid-date\"}";
        assertThrows(Exception.class, () -> mapper.readValue(json, TestBean.class));
    }
}