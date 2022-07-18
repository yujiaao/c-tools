package com.bixuebihui.db;

import com.bixuebihui.DbException;
import com.bixuebihui.datasource.DataSourceTest;
import com.bixuebihui.datasource.DbcpDataSource;
import com.bixuebihui.jdbc.AbstractBaseDao;
import com.bixuebihui.jdbc.DbHelper;
import com.bixuebihui.jdbc.IDbHelper;
import com.bixuebihui.jdbc.entity.CountObject;
import com.bixuebihui.jdbc.entity.CountValue;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

public class ActiveRecordImplTest extends TestCase {
	class T {
		Long id;
		String name;
		public String toString(){
			return id+" "+name;
		}
	}

	class BD extends AbstractBaseDao {
		public BD(IDbHelper db) {
			super(db);
		}

		@Override
		public T mapRow(ResultSet rs, int index, Set<String> set) throws SQLException {
			T t = new T();
			t.id = rs.getLong("id");
			t.name = rs.getString("name");
			return t;
		}

		@Override
		public String getTableName() {
			return "test";
		}
	}

	Connection cn;
	AbstractBaseDao bd;

	public void setUp() {
		DbcpDataSource ds = new DbcpDataSource();
		ds.setDatabaseConfig(DataSourceTest.getConfigDerby());
		DbHelper db = new DbHelper();
		db.setDataSource(ds);
		cn = db.getConnection();
		try {
			db.executeNoQuery("drop table test", cn);
			db.executeNoQuery("drop table test1", cn);
		}catch (DbException e){}
		db.executeNoQuery(
				"create table test(id int, name varchar(100), value int default 0, createtime timestamp)", cn);
		db.executeNoQuery(
				"create table test1(id int, name varchar(100))", cn);
		bd = new BD(db);
		bd.getDbHelper().executeNoQuery(
				"insert into test(id,name, createtime)values(123,'bac',"+
				bd.getTimestampSql("2016-1-1 12:10:11.0")
		+")");
		bd.getDbHelper().executeNoQuery(
				"insert into test(id,name, createtime)values(234,'bac',"+
						bd.getTimestampSql("2017-10-10 01:01:01.0")
				+")");
		bd.getDbHelper().executeNoQuery(
				"insert into test1(id,name)values(123,'bacqqq')");
		bd.getDbHelper().executeNoQuery(
				"insert into test1(id,name)values(234,'bacddd')");

		// boolean res = JDBCUtils.tableOrViewExists(null, null, "%", cn);
	}

	public void testAlias() {

		Object obj = bd.ar().alias("t1").eq("t1.id", 123).find();
		System.out.println(((T) obj).id);
		assertEquals(123L, ((T) obj).id.longValue());
	}

	public void testFields() {
		Object obj = bd.ar().alias("t1").fields("name,id").eq("t1.id", 123).find();
		System.out.println(((T) obj).name);
		assertEquals("bac", ((T) obj).name);
	}

	public void testInc() {
		int res = bd.ar().in("id",new Object[]{123,234}).inc("value");
		System.out.println(res);
		assertEquals(2, res);
		long inc = Long.parseLong(bd.ar().eq("ID", 123).get("value").toString());
		assertEquals(1, inc);
	}

	public void testEmptyStringAsNullCondition() {
		StringBuffer res1 = bd.ar().emptyStringAsNullCondition()
				.eq("name","")
				.eq("id", null)
				.eq("value","1").getSql().getCondition();
		assertEquals(" where value = ?", res1.toString());

		StringBuffer res = bd.ar()
				.eq("name","")
				.eq("id", null)
				.eq("value","1").getSql().getCondition();

		assertEquals(" where name = ? and value = ?", res.toString());


	}

	public void testUpdate() {
		int res = bd.ar()
				.in("id",new Object[]{123,234})
				.update(new String[]{"value", "name"},
						new Object[]{new SqlString("value+1"), "wow*"});
		System.out.println(res);
		assertEquals(2, res);
		long inc = Long.parseLong(bd.ar().eq("ID", 123).get("value").toString());
		assertEquals(1, inc);
	}

	public void testJoin() {

		Object obj = bd.ar().alias("t1").fields("t1.*,t2.name anothor_name ").eq("t1.id", 123).join("left join test1 t2 on t1.id=t2.id").find();

		assertEquals(123L, ((T) obj).id.longValue());

		ActiveRecord<Object> ar = bd.ar();
        // Record<Object> ar1 =ar.eq("id", 123).clone();

		Object obj1 = ar.eq("id", 123).find();
		System.out.println(((T) obj1).id);
		int res1 = ar.last().count();
		assertEquals(1, res1);
	}



	public void testTableNameOverride() {

		String obj = ((ActiveRecordImpl) bd.ar().tableName("t1")).getVectorSql("abc");
		assertEquals("select abc from t1", obj.trim());

	}


	public void testCountSum() {

		 CountValue res = bd.ar().countSum("id");

		assertEquals(357.0, res.getValue());

		assertEquals(2, res.getCount());
	}


	public void testCountObject() {

		 String field="createtime";
		GroupFunction fun = GroupFunction.MAX;

		CountObject<Timestamp> res = bd.ar().countObject(field, fun , Timestamp.class);

		assertEquals("2017-10-10 01:01:01.0", res.getValue().toString());

		assertEquals(2, res.getCount());

		fun = GroupFunction.MIN;
		res = bd.ar().countObject(field, fun , Timestamp.class);


		assertEquals("2016-01-01 12:10:11.0", res.getValue().toString());

		assertEquals(2, res.getCount());

	}

	public void testOrCond() {
		ActiveRecord<Object> ar = bd.ar().eq("id", 123)
				.or(bd.ar().eq("name", "bacddd").getCondStack())
				.ne("id", 2);

		assertEquals(" where (id = ? and id != ?) or (name = ?)",
				ar.getSql().getCondition().toString());


		List<Object> r = bd.ar().findAll();
		for(Object o:r)
		System.out.println(o);

		ar = bd.ar().eq("name", "bac");

		ar.eq("id",123).or(bd.ar().eq("name", "bacddd").eq("id", 234).getCondStack());

		assertEquals(" where (name = ? and id = ?) or (name = ? and id = ?)",
				ar.getSql().getCondition().toString());

		boolean		res = ar.exists();

		assertTrue(res);


	}


//	@Test
//	public void testSortIn() {
//		Object[] in = new Object[]{3, 1, 2};
//		ActiveRecord<Object> ar = bd.ar().alias("t1").in("t1.id", in).orderByIn("t1.id", in);
//		SqlHelper t1 =ar.getCondStack();
//
//		ar.getSql();
//
//
//		assertEquals(3, t1.build().getParams().size());
//		assertEquals(6, ((ActiveRecordImpl)ar).getParams().size());
//
//		assertEquals(" where t1.id in (?,?,?)", t1.toCondition().getCondition().toString());
//		assertEquals(" order by  field( t1.id,?,?,?)", ((ActiveRecordImpl<?, ?>) ar).parseOrder());
//		//List<Object> res = ar.findAll();
//
//	}



	/*
	public void testIgnoreBlank() throws SQLException {
		ActiveRecord<Object> ar = bd.ar().ignoreBlank(true).eq("id", null)
				.or(bd.ar().ignoreBlank(true).eq("name", null).eq("test", "abc").getCondStack())
				.ne("id", 2);

		assertEquals(" where (id != ?) or (test = ?)",
				ar.getSql().getCondition().toString());

		ar = bd.ar().ignoreBlank(false).eq("id", null)
				.or(bd.ar().eq("name", null).eq("test", "abc").getCondStack())
				.ne("id", 2);

		assertEquals(" where (id = null and id != ?) or (test = ?)",
				ar.getSql().getCondition().toString());

	}*/

}
