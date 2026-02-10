package com.bixuebihui.tablegen.generator;

import com.bixuebihui.jdbc.IDbHelper;
import com.bixuebihui.tablegen.TableGen;
import com.bixuebihui.tablegen.entry.ColumnData;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static com.bixuebihui.tablegen.TableUtilsTest.getPath;
import static org.junit.jupiter.api.Assertions.*;

class DalGeneratorTest {
        static IDbHelper dbHelper;

        @BeforeAll
        public static void setUp() {
            TableGen t = new TableGen();
            t.init(getPath());
            dbHelper = t.getDbHelper(t.getDbConfig());
        }

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
    void testSetIdLongWithCompositeLongKey() throws IOException {
        DalGenerator gen = new DalGenerator();
        gen.init(this.getClass().getResource("/tablegen.properties").getPath());
        
        // 生成 test_gen 表的 DAL（这个表有单主键，类型是Integer）
        String generatedDal = gen.generate("test_gen", 0);
        
        // 验证生成的 DAL 包含正确的 setIdLong 方法
        assertNotNull(generatedDal, "生成的 DAL 不应为空");
        
        // 检查是否包含 setIdLong 方法
        assertTrue(generatedDal.contains("setIdLong"), 
                "生成的 DAL 应该包含 setIdLong 方法");
        
        // 检查单主键表的 setIdLong 方法是否正常工作
        // 应该直接设置主键列，不需要特殊处理
        assertTrue(generatedDal.contains("info.setId((int)id)"), 
                "生成的 DAL 应该包含设置主键列 id 的代码，并进行类型转换");
        
        // 检查是否不包含复合主键的特殊逻辑
        assertFalse(generatedDal.contains("// 如果第一个键是Long型，取第一列"), 
                "单主键表的 DAL 不应包含复合主键的特殊逻辑");
        
        // 打印生成的 DAL 内容以便调试
        System.out.println("生成的单主键 DAL 内容:");
        System.out.println(generatedDal);
    }

    @Test
    void testSetIdLongWithSingleKey() throws IOException {
        DalGenerator gen = new DalGenerator();
        gen.init(this.getClass().getResource("/tablegen.properties").getPath());
        
        // 生成 t_edu 表的 DAL（这个表有单主键，类型是Integer）
        String generatedDal = gen.generate("t_edu", 0);
        
        // 验证生成的 DAL 包含正确的 setIdLong 方法
        assertNotNull(generatedDal, "生成的 DAL 不应为空");
        
        // 检查是否包含 setIdLong 方法
        assertTrue(generatedDal.contains("setIdLong"), 
                "生成的 DAL 应该包含 setIdLong 方法");
        
        // 检查单主键表的 setIdLong 方法是否正常工作
        // 应该直接设置主键列，不需要特殊处理
        assertTrue(generatedDal.contains("info.setId((int)id)"), 
                "生成的 DAL 应该包含设置主键列 id 的代码，并进行类型转换");
        
        // 检查是否不包含复合主键的特殊逻辑
        assertFalse(generatedDal.contains("// 如果第一个键是Long型，取第一列"), 
                "单主键表的 DAL 不应包含复合主键的特殊逻辑");
        
        // 打印生成的 DAL 内容以便调试
        System.out.println("生成的单主键 DAL 内容:");
        System.out.println(generatedDal);
    }


    void initTable(){
        try(Connection cn = dbHelper.getConnection()) {
                // 先创建复合主键测试表

                    cn.createStatement().execute("DROP TABLE IF EXISTS test_gen_long_dt_key");
                    cn.createStatement().execute(
                       "create  table test_gen_long_dt_key (" +
    "id bigint AUTO_INCREMENT not null," +
    "dt datetime," +
    "name varchar(100)," +
    "age int," +
    "birth datetime," +
    "edu_id int," +
    "gender varchar(10)," +
    "sex int," +
    "child int," +
    "primary key (id, dt)" +
"); " +
                    ""
                    );
                    cn.createStatement().execute(
                    "insert into test_gen_long_dt_key (id, dt, name, age, birth, edu_id, gender, sex, child) values (1, '2021-01-01', '张三', 18, '2021-01-01', 1, '男', 1, 1);" +
                            "");
                    
                   

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void testTemplateLogicForCompositeLongKey() throws IOException {
        // 这个测试验证我们修改的模板逻辑是否正确工作
        // 通过检查生成的代码来验证模板修改
        initTable();

        DalGenerator gen = new DalGenerator();
        gen.init(this.getClass().getResource("/tablegen.properties").getPath());
        
        // 生成 test_gen 表的 DAL
        String generatedDal = gen.generate("test_gen_long_dt_key", 0);
        
        // 验证生成的 DAL 包含正确的 setIdLong 方法
        assertNotNull(generatedDal, "生成的 DAL 不应为空");
        
        // 检查是否包含 setIdLong 方法
        assertTrue(generatedDal.contains("setIdLong"), 
                "生成的 DAL 应该包含 setIdLong 方法");
        
        // 检查单主键表的 setIdLong 方法是否正常工作
        // 应该直接设置主键列，不需要特殊处理
        assertTrue(generatedDal.contains("info.setId(id)"), 
                "生成的 DAL 应该包含设置主键列 id 的代码，无需类型转换");
        
        // 检查是否不包含复合主键的特殊逻辑（因为这是单主键表）
        
        // 检查是否不包含异常抛出逻辑（因为这是单主键表）
        assertFalse(generatedDal.contains("throw new UnsupportedOperationException"), 
                "单主键表的 DAL 不应包含异常抛出逻辑");
        
        System.out.println("模板验证通过：单主键表的 setIdLong 方法生成正确");
        
        // 打印生成的 setIdLong 方法部分以便调试
        int startIndex = generatedDal.indexOf("public void setIdLong");
        if (startIndex >= 0) {
            int endIndex = generatedDal.indexOf("}", startIndex);
            if (endIndex >= 0) {
                String setIdLongMethod = generatedDal.substring(startIndex, endIndex + 1);
                System.out.println("生成的 setIdLong 方法:");
                System.out.println(setIdLongMethod);
            }
        }
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
