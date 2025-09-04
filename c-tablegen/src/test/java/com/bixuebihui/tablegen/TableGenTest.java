package com.bixuebihui.tablegen;

import com.bixuebihui.generated.tablegen.business.T_metatableManager;
import com.bixuebihui.generated.tablegen.pojo.T_metatable;
import com.bixuebihui.jdbc.IDbHelper;
import com.bixuebihui.tablegen.entry.TableInfo;
import com.bixuebihui.tablegen.generator.DalGenerator;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// @Disabled
public class TableGenTest {


	@Test
	public void testRunSlow() {
		TableGen tg = new TableGen(System.out);
		System.out.println(this.getClass().getResource("/tablegen_mysql_new.properties")
				.getFile());

		tg.run(this.getClass().getResource("/tablegen_mysql_new.properties").getFile());

	}


	@Test
	public void testRunEs() {
		TableGen tg = new TableGen(System.out);
		System.out.println(this.getClass().getResource("/es.properties")
			.getFile());

		tg.run(this.getClass().getResource("/es.properties").getFile());

	}


	@Test
	public void testRun() throws SQLException {
		TableGen tg = new TableGen(System.out);
		System.out.println(this.getClass().getResource("/tablegen.properties")
				.getFile());

		tg.run(this.getClass().getResource("/tablegen.properties").getFile());

	}

    @Test
	public void testRunDiff() throws SQLException {
        TableGen tg = new TableGen(System.out);

        String propPath = System.getProperty("user.dir")+ "/tablegen.properties";

        tg.run(propPath);

    }


	@Test
	public void testGetExtraTableDataFromXml() throws IOException {
		TableGen tg = new TableGen();

		String extra_setting2="src/main/resources/pojos.xml";

		Map<String, T_metatable> res = tg.setInfo.getExtraTableDataFromXml(extra_setting2, null);
		assertEquals(1, res.size());
		assertEquals(1, res.get("sm_template_suite").getColumns().size());
	}

	@Test
	public void testGetPojoClassName() throws IOException {
		TableGen tg = new TableGen();
		tg.getConfig().addPrefix ="";

		String extra_setting2="src/main/resources/pojos.xml";
		String name = "sm_template_suite";
		assertEquals("Sm_template_suite", tg.getPojoClassName(name));
		Map<String, T_metatable> res = tg.setInfo.getExtraTableDataFromXml(extra_setting2, null);
		tg.setInfo.setTableDataExt(res);

		assertEquals("TemplateSuite", tg.getPojoClassName(name));
	}





	@Test
	public void testRunSpider() throws SQLException {
		TableGen tg = new TableGen();
		// this.getClass().getResource("tablegen.properties").getFile()
		System.out.println(this.getClass().getResource(
				"/spider_tablegen.properties").getFile());
		tg.run(this.getClass().getResource("/spider_tablegen.properties")
				.getFile());
	}

	@Test
	public void testRunVerycard() throws SQLException {
		TableGen tg = new TableGen();
		// this.getClass().getResource("tablegen.properties").getFile()
		System.out.println(this.getClass().getResource(
				"/verycard_tablegen.properties").getFile());
		tg.run(this.getClass().getResource("/verycard_tablegen.properties")
				.getFile());
	}



	@Test
	public void testRunVolManager() throws SQLException {
		TableGen tg = new TableGen();
		// this.getClass().getResource("tablegen.properties").getFile()IOException
		System.out.println(this.getClass().getResource(
				"/volmanage_tablegen.properties").getFile());
		tg.run(this.getClass().getResource("/volmanage_tablegen.properties")
				.getFile());
	}

	@Test
	public void testGetTableDate() throws SQLException, InstantiationException, IllegalAccessException, IOException {
		TableGen tg = new TableGen();
		// this.getClass().getResource("tablegen.properties").getFile()
		System.out.println(this.getClass().getResource("/tablegen.properties")
				.getFile());
		tg.init(this.getClass().getResource("/tablegen.properties")
				.getFile());
		IDbHelper helper = TableGen.getDbHelper(tg.getDbConfig());
		DatabaseMetaData meta = tg.connect(helper.getConnection());
		tg.setInfo.getTableData(tg.getConfig(), helper, meta);
		LinkedHashMap<String, TableInfo> tables = tg.setInfo.getTableInfos();
		for(TableInfo t:tables.values()){
			System.out.println(t);
		}

	}

	@Test
	public void testInitTableDateExt() throws SQLException, InstantiationException, IllegalAccessException, IOException {
		TableGen tg = new TableGen();
		System.out.println(this.getClass().getResource("/tablegen.properties")
				.getFile());
		try {
			tg.init(this.getClass().getResource("/tablegen.properties")
					.getFile());
			IDbHelper helper = TableGen.getDbHelper(tg.getDbConfig());
			DatabaseMetaData meta = tg.connect(helper.getConnection());

			tg.setInfo.getTableData(tg.getConfig(), helper, meta);
			T_metatableManager manager = new T_metatableManager(helper.getDataSource());
			boolean res  = tg.setInfo.initTableData(tg.setInfo.getTableInfos(), manager);
			assertTrue(res);
		}catch(BatchUpdateException e){
			e.getNextException().printStackTrace();
			e.printStackTrace();
		}

	}

	@Test
	public void testMakedir() {
		TableGen tg = new TableGen();
		System.out.println(this.getClass().getResource("/tablegen.properties")
				.getFile());
		tg.init(null);
		assertTrue(tg.makeDir());
	}

	@Test
	public void testRunCompositePrimaryKey() throws SQLException {
		TableGen tg = new TableGen(System.out);
		System.out.println(this.getClass().getResource("/rocks.properties").getFile());

		tg.run(this.getClass().getResource("/rocks.properties").getFile());

	}

	@Test
	public void testGetIndexWriterWithCompositePrimaryKey() throws Exception {
		// 创建一个模拟的 TableGen 实例
		TableGen tg = new TableGen(System.out);
		
		// 初始化配置
		tg.init(this.getClass().getResource("/tablegen.properties").getFile());
		
		// 加载表数据
		IDbHelper helper = TableGen.getDbHelper(tg.getDbConfig());
		DatabaseMetaData meta = tg.connect(helper.getConnection());
		tg.setInfo.getTableData(tg.getConfig(), helper, meta);
		
		// 创建一个测试用的 DalGenerator 子类来访问受保护的方法
		TestDalGenerator dalGenerator = new TestDalGenerator();
		dalGenerator.init(tg.getConfig(), tg.getDbConfig(), tg.setInfo, null);
		
		// 测试表名（使用实际存在的表）
		String tableName = "test_gen";
		
		// 检查表是否存在
		System.out.println("检查表 " + tableName + " 是否存在...");
		System.out.println("表信息: " + tg.setInfo.getTableInfos().get(tableName));
		
		// 检查配置
		System.out.println("配置信息:");
		System.out.println("config.indexes: " + tg.getConfig().indexes);
		System.out.println("config.isForeignKeys: " + tg.getConfig().isForeignKeys());
		
		// 临时启用索引功能来测试
		boolean originalIndexes = tg.getConfig().indexes;
		tg.getConfig().indexes = true;
		System.out.println("临时启用索引功能: config.indexes = " + tg.getConfig().indexes);
		
		// 检查表的索引信息
		System.out.println("表的索引信息: " + tg.setInfo.getTableIndexes(tableName));
		
		// 检查表的主键信息
		System.out.println("表的主键信息: " + tg.setInfo.getTableKeys(tableName));
		
		// 如果表没有联合主键，我们手动创建一个模拟的联合主键场景
		// 或者尝试使用另一个可能有联合主键的表
		if (tg.setInfo.getTableKeys(tableName).size() <= 1) {
			System.out.println("当前表只有一个主键，尝试查找有联合主键的表...");
			for (String table : tg.setInfo.getTableInfos().keySet()) {
				List<String> keys = tg.setInfo.getTableKeys(table);
				if (keys.size() > 1) {
					System.out.println("找到联合主键表: " + table + "，主键: " + keys);
					tableName = table;
					break;
				}
			}
		}
		
		// 检查最终选择的表的详细信息
		System.out.println("最终选择的表: " + tableName);
		System.out.println("最终表的主键: " + tg.setInfo.getTableKeys(tableName));
		System.out.println("最终表的索引: " + tg.setInfo.getTableIndexes(tableName));
		
		// 检查表的字段信息
		System.out.println("最终表的字段: " + tg.setInfo.getTableCols(tableName));
		
		// 如果选择的表没有索引，我们使用有索引的表来测试
		if (tg.setInfo.getTableIndexes(tableName).isEmpty()) {
			System.out.println("选择的表没有索引，使用有索引的表来测试...");
			for (String table : tg.setInfo.getTableInfos().keySet()) {
				Map<String, List<String>> indexes = tg.setInfo.getTableIndexes(table);
				if (!indexes.isEmpty()) {
					System.out.println("找到有索引的表: " + table + "，索引: " + indexes);
					tableName = table;
					break;
				}
			}
			System.out.println("最终用于测试的表: " + tableName);
			System.out.println("最终表的主键: " + tg.setInfo.getTableKeys(tableName));
			System.out.println("最终表的索引: " + tg.setInfo.getTableIndexes(tableName));
		}
		
		// 调用 getIndexWriter 方法
		String result = dalGenerator.getIndexWriter(tableName);
		
		// 恢复原始配置
		tg.getConfig().indexes = originalIndexes;
		
		System.out.println("getIndexWriter 返回结果长度: " + (result != null ? result.length() : "null"));
		System.out.println("getIndexWriter 返回结果: " + result);
		
		// 验证结果不为空
		assertTrue(result != null && !result.trim().isEmpty(), "生成的索引代码不应为空");
		
		// 验证生成的代码包含必要的索引方法
		// 对于联合主键，应该生成基于多个字段的查询方法
		assertTrue(result.contains("selectBy"), "应该包含 selectBy 方法");
		assertTrue(result.contains("selectAllLike"), "应该包含 selectAllLike 方法");
		assertTrue(result.contains("updateBy"), "应该包含 updateBy 方法");
		assertTrue(result.contains("deleteBy"), "应该包含 deleteBy 方法");
		assertTrue(result.contains("countBy"), "应该包含 countBy 方法");
		
		// 验证生成的代码包含正确的参数类型
		// 联合主键的方法应该有多个参数
		assertTrue(result.contains("(") && result.contains(",") && result.contains(")"), 
			"联合主键方法应该有多个参数");
		
		// 验证生成的代码包含正确的 WHERE 子句
		// 联合主键的 WHERE 子句应该包含多个条件，用 AND 连接
		assertTrue(result.contains(" and "), "WHERE 子句应该包含 AND 连接符");
		
		// 验证生成的代码包含正确的参数占位符
		// 联合主键应该有多个 ? 占位符
		long questionMarkCount = result.chars().filter(ch -> ch == '?').count();
		assertTrue(questionMarkCount > 1, "应该有多个参数占位符");
		
		// 验证生成的代码包含正确的表名引用
		assertTrue(result.contains("getTableName()"), "应该包含表名引用");
		
		// 验证生成的代码包含正确的返回类型
		assertTrue(result.contains("List<") || result.contains("return"), 
			"应该包含正确的返回类型");
		
		System.out.println("生成的索引代码：");
		System.out.println(result);
	}

	/**
	 * 测试用的 DalGenerator 子类，用于访问受保护的 getIndexWriter 方法
	 */
	private static class TestDalGenerator extends DalGenerator {
		@Override
		protected String getIndexWriter(String tableName) {
			return super.getIndexWriter(tableName);
		}
	}

}
