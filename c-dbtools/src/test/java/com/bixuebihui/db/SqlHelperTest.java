package com.bixuebihui.db;

import junit.framework.TestCase;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

import static com.bixuebihui.db.Dialect.MYSQL;
import static com.bixuebihui.db.Dialect.ORACLE;

public class SqlHelperTest extends TestCase {

	public void testSqlHelper() {
		SqlHelper sp = new SqlHelper();
		assertNotNull(sp.filters);
		assertEquals(0,sp.filters.size());
	}

	public void testToCondition() {
		SqlHelper sp = new SqlHelper();
		SqlPocket res = sp.toCondition();
		String cond = res.getCondition().toString();
		assertEquals("", cond);
		assertEquals(0, res.getParams().size());
	}

	public void testToString() {
		SqlHelper sp = new SqlHelper();
		//System.out.println(sp.toString());
		assertEquals("",sp.toCondition().getCondition().toString());
	}

	public void testBuild() {
		SqlHelper sp = new SqlHelper();
		SqlPocket res = sp.build();
		assertNotNull(res);
	}


	public void testEqualMapOfStringObjectBoolean() {
		SqlHelper sp = new SqlHelper();
		Map<String,Object> condition = new Hashtable<>();
		condition.put("abc", 123);
		condition.put("abcd", "test");
		boolean ignoreNulls = true;
		sp.eq(condition, ignoreNulls);
		SqlPocket res = sp.build();
		assertEquals(" where abc = ? and abcd = ?", res.getCondition().toString());
	}

	public void testEqualMapOfStringObject() {
		SqlHelper sp = new SqlHelper();
		String name = "old";
		Object value = 1L;
		sp.eq(name, value);
		SqlPocket res = sp.build();
		assertEquals(" where old = ?", res.getCondition().toString());
	}

	public void testGetDatabaseType() {
		SqlHelper sp = new SqlHelper();
		int res = sp.getDatabaseType();
		assertEquals(MYSQL, res);
	}

	public void testOr() {
		SqlHelper sp = new SqlHelper();
		sp.eq("abc", 123);
		SqlHelper cond= new SqlHelper();
		cond.eq("test", "1234");
		sp.or(cond);
		SqlPocket res = sp.build();
		assertEquals(" where (abc = ?) or (test = ?)", res.getCondition().toString());
		assertEquals(2, res.getParams().size());
		assertEquals(123, res.getParams().get(0));
		assertEquals("1234", res.getParams().get(1));
	}

	public void testNot() {
		SqlHelper sp = new SqlHelper();
		sp.eq("abc", 123);
		SqlHelper cond= new SqlHelper();
		cond.eq("test", "123");
		sp.not(cond);
		SqlPocket res = sp.build();
		assertEquals(" where (abc = ?) and not (test = ?)", res.getCondition().toString());
	}

	public void testInStringCollectionOfObject() {
		SqlHelper sp = new SqlHelper();
		String field="abc";
		Collection<Object> value= Arrays.asList(new Object[]{1,2,3});
		sp.in(field, value);

		SqlPocket res = sp.build();
		assertEquals(" where abc in (?,?,?)", res.getCondition().toString());

		String field1="abc1";
		Collection<Object> value1= Arrays.asList(new Object[]{4,5,6});
		sp.in(field1, value1);

		 res = sp.build();
		assertEquals(" where abc in (?,?,?) and abc1 in (?,?,?)", res.getCondition().toString());
	}

	public void testInStringObjectArray() {
		SqlHelper sp = new SqlHelper();
		String field="abc";

		Object[] value= new Object[]{1,new Date(0),"3"};
		sp.in(field, value);
		sp.eq("test", 123);
		SqlPocket res = sp.build();
		assertEquals(" where abc in (?,?,?) and test = ?", res.getCondition().toString());

		sp.clear();
		value= new Object[]{1,2,1};
		sp.in(field, value);
		res = sp.build();
		assertEquals(" where abc in (?,?)", res.getCondition().toString());
		System.out.println(res.getParams());
	}

	public void testIsNull() {
		SqlHelper sp = new SqlHelper();
		sp.isNull("notfield");
		SqlPocket res = sp.build();
		assertEquals(" where notfield is null", res.getCondition().toString());
	}

	public void testAt() {
//		SqlHelper sp = new SqlHelper();
//		ISqlConditionType timespan =new Timespan();
//		String time;
//		sp.at(time,timespan );
//		SqlPocket res = sp.build();
//		assertEquals(" notfield is null", res.getCondition().toString());
	}

	public void testLike() {
		SqlHelper sp = new SqlHelper();
		sp.like("IK", "analyzer");
		SqlPocket res = sp.build();
		assertEquals(" where IK like concat('%',?,'%')", res.getCondition().toString());
	}

	public void testEqualStringObject() {
		SqlHelper sp = new SqlHelper();
		sp.eq("what", "is");
		SqlPocket res = sp.build();
		assertEquals(" where what = ?", res.getCondition().toString());
		assertEquals(1, res.getParams().size());
		assertEquals("is", res.getParams().get(0));
	}


	public void testEqualStringArrayStringArray() throws SQLException {
		SqlHelper sp = new SqlHelper();
		String[] field= new String[]{"bar","foo"};
		String[] value= new String[]{"a123","b456"};
		sp.eq(field, value);
		sp.eq("aq", "not");
		SqlPocket res = sp.build();
		assertEquals(" where bar = ? and foo = ? and aq = ?", res.getCondition().toString());
		assertEquals(3, res.getParams().size());
		assertEquals("not", res.getParams().get(2));
	}

	public void testGreaterThan() {
		SqlHelper sp = new SqlHelper();
		sp.greaterThan("abc", 123).smallerThan("abc", 456);
		sp.eq("aq", "not");
		SqlPocket res = sp.build();
		assertEquals(" where abc > ? and abc < ? and aq = ?", res.getCondition().toString());
		assertEquals(3, res.getParams().size());
		assertEquals("not", res.getParams().get(2));
		assertEquals(456, res.getParams().get(1));
	}

	public void testSmallerThan() {
		SqlHelper sp = new SqlHelper();
		sp.smallerThan("abc", 456);
		SqlPocket res = sp.build();
		assertEquals(" where abc < ?", res.getCondition().toString());
		assertEquals(1, res.getParams().size());
		assertEquals(456, res.getParams().get(0));
	}

	public void testSetDatabaseType() {
		SqlHelper sp = new SqlHelper();
		sp.setDatabaseType(ORACLE);
		assertEquals(ORACLE, sp.getDatabaseType());
	}

	public void testIsUseNullAsCondition() {
		SqlHelper sp = new SqlHelper();

		assertEquals(false, sp.isUseNullAsCondition());
	}

	public void testSetUseNullAsCondition() {
		SqlHelper sp = new SqlHelper();
		sp.setUseNullAsCondition(true);
		assertEquals(true, sp.isUseNullAsCondition());
	}

	public void testClear() throws SQLException {
		SqlHelper sp = new SqlHelper();
		String[] field= new String[]{"bar","foo"};
		String[] value= new String[]{"a123","b456"};
		sp.eq(field, value);
		sp.eq("aq", "not");
		SqlPocket res = sp.build();
		assertEquals(" where bar = ? and foo = ? and aq = ?", res.getCondition().toString());
		assertEquals(3, res.getParams().size());
		assertEquals("not", res.getParams().get(2));
		sp.clear();
		res = sp.build();
		assertEquals(0, res.getParams().size());
		assertEquals("", res.getCondition().toString());
	}



	public void testTrimWhere(){
		SqlHelper sp = new SqlHelper();
		StringBuffer trimCond= new StringBuffer(" where bar = ? and foo = ? and aq = ?");
		StringBuffer res = sp.trimWhere(trimCond);
		assertEquals("bar = ? and foo = ? and aq = ?", res.toString());
	}


	public void testCopyConstructor() throws SQLException {
		SqlHelper sp = new SqlHelper();
		String[] field= new String[]{"bar","foo"};
		String[] value= new String[]{"a123","b456"};
		sp.eq(field, value);
		SqlHelper res = new SqlHelper(sp);
		assertEquals(" where bar = ? and foo = ?", sp.build().getCondition().toString());
		sp.clear();
		assertEquals(" where bar = ? and foo = ?", res.build().getCondition().toString());
	}


	public void testBiFilterWithSqlObject() throws SQLException {
		SqlHelper sp = new SqlHelper();
		String[] field= new String[]{"bar","foo"};
		Object[] value= new Object[]{"a123",new SqlString("foo+2")};
		sp.eq(field, value);
		assertEquals(" where bar = ? and foo = foo+2", sp.build().getCondition().toString());

		sp = new SqlHelper();
		sp.in("abc", new SqlString("select abc from def"));

		assertEquals(" where abc in (select abc from def)", sp.build().getCondition().toString());

	}
}
