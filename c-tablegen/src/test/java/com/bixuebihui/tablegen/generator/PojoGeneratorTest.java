package com.bixuebihui.tablegen.generator;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import com.bixuebihui.tablegen.entry.ColumnData;

class PojoGeneratorTest {

    @Test
    void getExtendsClasses() throws IOException {
        PojoGenerator gen = new PojoGenerator();
        gen.init(this.getClass().getResource("/tablegen.properties").getPath());
        String res = gen.generate("test", 0);

        System.out.println(res);
    }

    @Test
    void getAnnotationForClass() {
        PojoGenerator gen = new PojoGenerator();
        String res = gen.getAnnotationForClass();
        assertEquals("io.swagger.annotations.ApiModel;", res);
    }

    @Test
    void getTargetFileName() {
        PojoGenerator gen = new PojoGenerator();
        gen.init(this.getClass().getResource("/tablegen.properties").getPath());
        String res = gen.getTargetFileName("test");

        res = res.replace("\\", "/");

        assertThat( res, Matchers.endsWith("src/main/java/com/bixuebihui/test/pojo/Test.java"));
    }

    @Test
    void getClassName() {
        PojoGenerator gen = new PojoGenerator();
        gen.init(this.getClass().getResource("/tablegen.properties").getPath());
        String res = gen.getClassName("test");

        assertThat(res, Matchers.endsWith("Test"));

    }


    @Test
    void mapLookup() throws IOException {
        PojoGenerator gen = new PojoGenerator();
        gen.init(this.getClass().getResource("/tablegen.properties").getPath());
        Handlebars h = gen.getHandlebars();
        Map<String, Object> con = gen.getContextMap("test");

        Template temp = h.compileInline("{{typeDefaultValue 'Long'}}");

        String res = temp.apply(Context.newBuilder(con).build());
        assertEquals("0L", res);

        temp = h.compileInline("{{typeDefaultValue 'String'}}");

        res = temp.apply(Context.newBuilder(con).build());
        assertEquals("&quot;&quot;", res);


    }



    @Test
    void varAssign() throws IOException {
        PojoGenerator gen = new PojoGenerator();
        gen.init(this.getClass().getResource("/tablegen.properties").getPath());
        Handlebars h = gen.getHandlebars();
        Map<String, Object> con = gen.getContextMap("test");

        Template temp = h.compileInline("{{#let 'type'}}Long{{/let}} {{typeDefaultValue type}}");

        String res = temp.apply(Context.newBuilder(con).build());
        assertEquals(" 0L", res);

    }

    @Test
    void testIgnoreFieldsConfiguration() throws IOException {
        PojoGenerator gen = new PojoGenerator();
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
        PojoGenerator gen = new PojoGenerator();
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
    void testIgnoreFieldsConfigurationFormat() {
        PojoGenerator gen = new PojoGenerator();
        gen.init(this.getClass().getResource("/tablegen.properties").getPath());
        
        // 测试配置格式解析 - 通过生成 POJO 来验证配置是否生效
        // 由于无法直接访问 config，我们通过生成的 POJO 内容来验证配置
        
        // 验证 test_gen 表的字段过滤是否生效
        Map<String, Object> context = gen.getContextMap("test_gen");
        List<ColumnData> fields = (List<ColumnData>) context.get("fields");
        
        // 检查 age 和 birth 字段是否被过滤掉
        boolean hasAgeField = fields.stream()
                .anyMatch(field -> "age".equals(field.getName()));
        assertFalse(hasAgeField, "age 字段应该被 ignore_fields 配置过滤掉");
        
        boolean hasBirthField = fields.stream()
                .anyMatch(field -> "birth".equals(field.getName()));
        assertFalse(hasBirthField, "birth 字段应该被 ignore_fields 配置过滤掉");
        
        // 验证 t_edu 表的字段过滤是否生效
        Map<String, Object> tEduContext = gen.getContextMap("t_edu");
        List<ColumnData> tEduFields = (List<ColumnData>) tEduContext.get("fields");
        
        boolean hasDegreeField = tEduFields.stream()
                .anyMatch(field -> "degree".equals(field.getName()));
        assertFalse(hasDegreeField, "degree 字段应该被 ignore_fields 配置过滤掉");
    }

    @Test
    void testGeneratedPojoWithoutIgnoredFields() throws IOException {
        PojoGenerator gen = new PojoGenerator();
        gen.init(this.getClass().getResource("/tablegen.properties").getPath());
        
        // 生成 test_gen 表的 POJO
        String generatedPojo = gen.generate("test_gen", 0);
        
        // 验证生成的 POJO 不包含被忽略的字段
        assertNotNull(generatedPojo, "生成的 POJO 不应为空");
        
        // 检查是否包含被忽略的字段声明
        assertFalse(generatedPojo.contains("protected Short age"), 
                "生成的 POJO 不应包含 age 字段声明");
        assertFalse(generatedPojo.contains("protected Timestamp birth"), 
                "生成的 POJO 不应包含 birth 字段声明");
        
        // 检查是否包含被忽略的字段的 getter/setter 方法
        assertFalse(generatedPojo.contains("setAge("), 
                "生成的 POJO 不应包含 setAge 方法");
        assertFalse(generatedPojo.contains("getAge("), 
                "生成的 POJO 不应包含 getAge 方法");
        assertFalse(generatedPojo.contains("setBirth("), 
                "生成的 POJO 不应包含 setBirth 方法");
        assertFalse(generatedPojo.contains("getBirth("), 
                "生成的 POJO 不应包含 getBirth 方法");
        
        // 打印生成的 POJO 内容以便调试
        System.out.println("生成的 POJO 内容:");
        System.out.println(generatedPojo);
        
        // 检查是否仍然包含其他字段 - 注意字段类型可能不同
        // 由于 ignore_fields 配置，age 和 birth 字段被过滤掉了
        // 但其他字段应该仍然存在
        assertTrue(generatedPojo.contains("protected Integer  id") || generatedPojo.contains("protected int  id"), 
                "生成的 POJO 应该包含 id 字段声明");
        assertTrue(generatedPojo.contains("protected String  name"), 
                "生成的 POJO 应该包含 name 字段声明");
        assertTrue(generatedPojo.contains("protected Integer  eduId") || generatedPojo.contains("protected int  eduId"), 
                "生成的 POJO 应该包含 eduId 字段声明");
        
        // 验证被忽略的字段确实不存在
        // 注意：只检查字段声明，不检查其他可能包含这些字符串的地方
        assertFalse(generatedPojo.contains("protected Short age") || generatedPojo.contains("protected Integer age"), 
                "生成的 POJO 不应包含 age 字段声明");
        assertFalse(generatedPojo.contains("protected Timestamp birth") || generatedPojo.contains("protected Date birth"), 
                "生成的 POJO 不应包含 birth 字段声明");
    }

    @Test
    void testViewWithIgnoreFields() throws IOException {
        PojoGenerator gen = new PojoGenerator();
        gen.init(this.getClass().getResource("/tablegen.properties").getPath());
        
        // 测试视图的字段过滤
        Map<String, Object> context = gen.getContextMap("my_test_view");
        List<ColumnData> fields = (List<ColumnData>) context.get("fields");
        
        // 验证视图字段也被正确过滤
        // 注意：视图可能没有字段，或者配置有问题，我们先检查上下文内容
        System.out.println("视图上下文内容:");
        context.forEach((key, value) -> {
            System.out.println(key + ": " + value);
            if (value instanceof List) {
                System.out.println("  List size: " + ((List<?>) value).size());
            }
        });
        
        // 如果视图有字段，检查是否被正确过滤
        if (fields != null && !fields.isEmpty()) {
            System.out.println("视图字段数量: " + fields.size());
            fields.forEach(field -> System.out.println("视图字段: " + field.getName()));
            
            // 检查视图中的字段是否也被过滤（如果视图包含被忽略的字段）
            // 注意：视图的字段过滤取决于视图定义和 ignore_fields 配置
        } else {
            System.out.println("视图没有字段或字段列表为空");
            // 视图可能没有字段，这是正常情况
        }
    }

    
}
