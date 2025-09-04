package com.bixuebihui.tablegen.generator;

import com.bixuebihui.tablegen.entry.ColumnData;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DalGeneratorTest {
    @Test
    void getExtendsClasses() throws IOException {
        DalGenerator gen = new DalGenerator();
        gen.init(this.getClass().getResource("/tablegen.properties").getPath());
        String res = gen.generate("test_gen", 0);

        System.out.println(res);
    }

    @Test
    void testIgnoreFieldsConfiguration() throws IOException {
        DalGenerator gen = new DalGenerator();
        gen.init(this.getClass().getResource("/tablegen.properties").getPath());
        
        // 测试 test_gen 表的字段过滤
        Map<String, Object> context = gen.getContextMap("test_gen");
        List<ColumnData> fields = (List<ColumnData>) context.get("fields");
        
        // 验证被忽略的字段不在生成的字段列表中
        assertNotNull(fields, "字段列表不应为空");
        
        // 检查 age 字段是否被过滤掉
        boolean hasAgeField = fields.stream()
                .anyMatch(field -> "age".equals(field.getName()));
        assertFalse(hasAgeField, "age 字段应该被 ignore_fields 配置过滤掉");
        
        // 检查 birth 字段是否被过滤掉
        boolean hasBirthField = fields.stream()
                .anyMatch(field -> "birth".equals(field.getName()));
        assertFalse(hasBirthField, "birth 字段应该被 ignore_fields 配置过滤掉");
        
        // 检查其他字段是否仍然存在
        boolean hasIdField = fields.stream()
                .anyMatch(field -> "id".equals(field.getName()));
        assertTrue(hasIdField, "id 字段应该仍然存在");
        
        boolean hasNameField = fields.stream()
                .anyMatch(field -> "name".equals(field.getName()));
        assertTrue(hasNameField, "name 字段应该仍然存在");
        
        boolean hasEduIdField = fields.stream()
                .anyMatch(field -> "edu_id".equals(field.getName()));
        assertTrue(hasEduIdField, "edu_id 字段应该仍然存在");
    }

    @Test
    void testIgnoreFieldsForDifferentTable() throws IOException {
        DalGenerator gen = new DalGenerator();
        gen.init(this.getClass().getResource("/tablegen.properties").getPath());
        
        // 测试 t_edu 表的字段过滤
        Map<String, Object> context = gen.getContextMap("t_edu");
        List<ColumnData> fields = (List<ColumnData>) context.get("fields");
        
        // 验证被忽略的字段不在生成的字段列表中
        assertNotNull(fields, "字段列表不应为空");
        
        // 检查 degree 字段是否被过滤掉
        boolean hasDegreeField = fields.stream()
                .anyMatch(field -> "degree".equals(field.getName()));
        assertFalse(hasDegreeField, "degree 字段应该被 ignore_fields 配置过滤掉");
        
        // 检查 id 字段是否仍然存在
        boolean hasIdField = fields.stream()
                .anyMatch(field -> "id".equals(field.getName()));
        assertTrue(hasIdField, "id 字段应该仍然存在");
    }

    @Test
    void testGeneratedDalWithoutIgnoredFields() throws IOException {
        DalGenerator gen = new DalGenerator();
        gen.init(this.getClass().getResource("/tablegen.properties").getPath());
        
        // 生成 test_gen 表的 DAL
        String generatedDal = gen.generate("test_gen", 0);
        
        // 验证生成的 DAL 不包含被忽略的字段
        assertNotNull(generatedDal, "生成的 DAL 不应为空");
        
        // 检查是否包含被忽略的字段的引用
        assertFalse(generatedDal.contains("getAge("), 
                "生成的 DAL 不应包含 getAge 方法调用");
        assertFalse(generatedDal.contains("getBirth("), 
                "生成的 DAL 不应包含 getBirth 方法调用");
        
        // 检查是否仍然包含其他字段的引用
        assertTrue(generatedDal.contains("getId("), 
                "生成的 DAL 应该包含 getId 方法调用");
        assertTrue(generatedDal.contains("getName("), 
                "生成的 DAL 应该包含 getName 方法调用");
        
        // 打印生成的 DAL 内容以便调试
        System.out.println("生成的 DAL 内容:");
        System.out.println(generatedDal);
    }

    @Test
    void isNotEmpty() {
    }

    @Test
    void getColType() {
    }

    @Test
    void getFirstKeyType() {
    }

    @Test
    void getFirstKeyName() {
    }

    @Test
    void containsVersion() {
    }

    @Test
    void makeInsertObjects() {
    }

    @Test
    void makeUpdateObjects() {
    }

    @Test
    void createKeyObjects() {
    }

    @Test
    void mapRow() {
    }

    @Test
    void testGetFirstKeyType() {
    }

    @Test
    void testGetFirstKeyName() {
    }
}
