package com.bixuebihui.test.converter;

import com.bixuebihui.test.dto.TestGenDto;
import com.bixuebihui.test.pojo.TestGen;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MapStruct 生成实现类，无需 Spring 容器；避免 @SpringBootTest 拉全量上下文并依赖本机 MySQL。
 */
class TestGenConverterTest {
    // 使用 MapStruct 的静态实例
    private final TestGenConverter testGenConverter = TestGenConverter.MAPPER;


    @Test
    void toDto() {
        TestGen info = new TestGen();
        info.setName("test name");

        TestGenDto res = testGenConverter.toDto(info);
        assertNotNull(res);
        assertEquals("test name", res.getName());
    }

    @Test
    void toDao() {
        TestGenDto dto = new TestGenDto();
        dto.setName("test dto name");

        TestGen res = testGenConverter.toDao(dto);
        assertNotNull(res);
        assertEquals("test dto name", res.getName());
    }

    @Test
    void toDtoList() {
        TestGen info1 = new TestGen();
        info1.setName("name1");
        TestGen info2 = new TestGen();
        info2.setName("name2");
        
        List<TestGen> input = Arrays.asList(info1, info2);
        List<TestGenDto> res = testGenConverter.toDtoList(input);
        
        assertNotNull(res);
        assertEquals(2, res.size());
        assertEquals("name1", res.get(0).getName());
        assertEquals("name2", res.get(1).getName());
    }

    @Test
    void toDaoList() {
        TestGenDto dto1 = new TestGenDto();
        dto1.setName("dto1");
        TestGenDto dto2 = new TestGenDto();
        dto2.setName("dto2");
        
        List<TestGenDto> input = Arrays.asList(dto1, dto2);
        List<TestGen> res = testGenConverter.toDaoList(input);
        
        assertNotNull(res);
        assertEquals(2, res.size());
        assertEquals("dto1", res.get(0).getName());
        assertEquals("dto2", res.get(1).getName());
    }
}
