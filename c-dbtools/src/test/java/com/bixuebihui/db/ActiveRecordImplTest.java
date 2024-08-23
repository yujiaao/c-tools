package com.bixuebihui.db;

import com.bixuebihui.datasource.DataSourceTest;
import com.bixuebihui.datasource.DbcpDataSource;
import com.bixuebihui.jdbc.AbstractBaseDao;
import com.bixuebihui.jdbc.DbHelper;
import com.bixuebihui.jdbc.IDbHelper;
import com.bixuebihui.jdbc.entity.CountObject;
import com.bixuebihui.jdbc.entity.CountValue;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.bixuebihui.jdbc.BaseDao.sql;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ActiveRecordImplTest {


    Connection cn;
    AbstractBaseDao bd;

    @BeforeEach
    protected void setUp() {
        DbcpDataSource ds = new DbcpDataSource();
       // ds.setDatabaseConfig(DataSourceTest.getConfigDerby());
        ds.setDatabaseConfig(DataSourceTest.getConfig());
        DbHelper db = new DbHelper();
        db.setDataSource(ds);
        cn = db.getConnection();
        try {
        	db.executeNoQuery("drop table test", cn);
        	db.executeNoQuery("drop table test1", cn);
        }catch (Exception e){}
        try{
        db.executeNoQuery(
                "create table test(id int, name varchar(100), value int default 0, createtime timestamp)", cn);
        db.executeNoQuery(
                "create table test1(id int, name varchar(100))", cn);
        }catch (Exception e){
        }
        bd = new BD(db);
        bd.getDbHelper().executeNoQuery(
                "insert into test(id,name, createtime)values(123,'bac'," +
                        bd.getTimestampSql("2016-1-1 12:10:11.0")
                        + ")");
        bd.getDbHelper().executeNoQuery(
                "insert into test(id,name, createtime)values(234,'bac'," +
                        bd.getTimestampSql("2017-10-10 01:01:01.0")
                        + ")");
        bd.getDbHelper().executeNoQuery(
                "insert into test1(id,name)values(123,'bacqqq')");
        bd.getDbHelper().executeNoQuery(
                "insert into test1(id,name)values(234,'bacddd')");

        // boolean res = JDBCUtils.tableOrViewExists(null, null, "%", cn);
    }

    @Test
    public void testAlias() {
        Object obj = bd.ar().alias("t1").eq("t1.id", 123).find();
        assertEquals(123L, ((T) obj).id.longValue());
    }

    @Test
    public void testFields() {
        Object obj = bd.ar().alias("t1").fields("name,id").eq("t1.id", 123).find();
        assertEquals("bac", ((T) obj).name);
    }

    @Test
    public void testCsv() {
        bd.getDbHelper().executeNoQuery("insert into test(id,name)values(234,'ba\"c')");

        String s = bd.ar().alias("t1")
                .fields("name,id").eq("t1.id", 234).findAllToCsv("何人,ID", "name,id");
        assertEquals("何人,ID\n" +
                "bac,234\n" +
                "\"ba\"\"c\",234\n", s);
    }

    @Test
    public void testCsvForExcel() {
        bd.getDbHelper().executeNoQuery("insert into test(id,name, createtime)values(234,'ba\"c', " +
                "'2023-07-09 12:23:34.0')");

        String s = bd.ar().alias("t1")
                .fields("name,id,createtime")
                .eq("t1.id", 234)
                .findAllToCsv("何人,ID,何时", "name,id,createtime", false, true);
        assertEquals("何人,ID,何时\n" +
                "bac,234,\"=\"\"2017-10-10 01:01:01.0\"\"\"\n" +
                "\"ba\"\"c\",234,\"=\"\"2023-07-09 12:23:34.0\"\"\"\n", s);
        System.out.println(s);
    }

    @Test
    public void testCsvWithFieldNames() {
        bd.getDbHelper().executeNoQuery("insert into test(id,name)values(234,'ba\"c')");

        String s = bd.ar().alias("t1").fields("name,id").eq("t1.id", 234).findAllToCsv("何人,ID", "name,id");
        assertEquals("何人,ID\n" +
                "bac,234\n" +
                "\"ba\"\"c\",234\n", s);
    }

    @Test
    public void testInc() {
        int res = bd.ar().in("id", new Object[]{123, 234}).inc("value");
        assertEquals(2, res);
        long inc = Long.parseLong(bd.ar().eq("ID", 123).get("value").toString());
        assertEquals(1, inc);
    }

    @Test
    public void testEmptyStringAsNullCondition() {
        StringBuffer res1 = bd.ar().emptyStringAsNullCondition()
                .eq("name", "")
                .eq("id", null)
                .eq("value", "1").getSql().getCondition();
        assertEquals(" where value = ?", res1.toString());

        StringBuffer res = bd.ar()
                .eq("name", "")
                .eq("id", null)
                .eq("value", "1").getSql().getCondition();

        assertEquals(" where name = ? and value = ?", res.toString());


    }

    @Test
    public void testUpdate() {
        int res = bd.ar()
                .in("id", new Object[]{123, 234})
                .update(new String[]{"value", "name"},
                        new Object[]{new SqlString("value+1"), "wow*"});
        assertEquals(2, res);
        long inc = Long.parseLong(bd.ar().eq("ID", 123).get("value").toString());
        assertEquals(1, inc);
    }

    @Test
    public void testJoin() {

        Object obj = bd.ar().alias("t1").fields("t1.*,t2.name anothor_name ").eq("t1.id", 123).join("left join test1 t2 on t1.id=t2.id").find();

        assertEquals(123L, ((T) obj).id.longValue());

        ActiveRecord<Object> ar = bd.ar();

        Object obj1 = ar.eq("id", 123).find();
        assertEquals(123L, (long) ((T) obj1).id);
        int res1 = ar.last().count();
        assertEquals(1, res1);
    }

    @Test
    public void testTableNameOverride() {

        String obj = ((ActiveRecordImpl) bd.ar().tableName("t1").limit(10, 20))
                .getVectorSql("abc");
        assertEquals("select abc from t1     limit 10,20", obj.trim());

    }

    @Test
    public void testCountSum() {

        CountValue res = bd.ar().countSum("id");

        assertEquals(357.0, res.getValue());

        assertEquals(2, res.getCount());
    }

    @Test
    public void testCountObject() {

        String field = "createtime";
        GroupFunction fun = GroupFunction.MAX;

        CountObject<Timestamp> res = bd.ar().countObject(field, fun, Timestamp.class);

        assertEquals("2017-10-10 01:01:01.0", res.getValue().toString());

        assertEquals(2, res.getCount());

        fun = GroupFunction.MIN;
        res = bd.ar().countObject(field, fun, Timestamp.class);


        assertEquals("2016-01-01 12:10:11.0", res.getValue().toString());

        assertEquals(2, res.getCount());

    }

    @Test
    public void testOrCond() {
        ActiveRecord<Object> ar = bd.ar().eq("id", 123)
                .or(bd.ar().eq("name", "bacddd").getCondStack())
                .ne("id", 2);

        assertEquals(" where (id = ? and id != ?) or (name = ?)",
                ar.getSql().getCondition().toString());


        List<Object> r = bd.ar().findAll();
        for (Object o : r)
            System.out.println(o);

        ar = bd.ar().eq("name", "bac");

        ar.eq("id", 123).or(bd.ar().eq("name", "bacddd").eq("id", 234).getCondStack());

        assertEquals(" where (name = ? and id = ?) or (name = ? and id = ?)",
                ar.getSql().getCondition().toString());

        boolean res = ar.exists();

        assertTrue(res);
    }

    @Test
    public void testAndCond() {
        SqlPocket sql = bd.ar().eq("id", 123)
                .and(bd.ar().eq("name", "a").or(bd.ar().eq("pub", 1).eq("id", 0))).getSql();


        assertEquals(" where (id = ?) and ((name = ?) or (pub = ? and id = ?))",
                sql.getCondition().toString());
        assertEquals(4, sql.getParams().size());
        assertEquals("a", sql.getParams().get(1));

    }

    @Test
    void or() {
        SqlPocket sql = bd.ar()
                .in(Pair.of("id", "name"),
                        List.of(Pair.of("1", "张三"),
                                Pair.of("2", "李四"))).getSql();


        assertEquals(" where ((id = ? and name = ?) or (id = ? and name = ?))",
                sql.getCondition().toString());
        assertEquals(4, sql.getParams().size());
        assertEquals("张三", sql.getParams().get(1));
    }

    @Test
    void and() {

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

//	@Test
//	void testGroupBy() throws SQLException{
////		SELECT
////				[GroupName],
////					[Content],
////					[SendType],
////					[Remark],
////		COUNT (0) AS GroupCount,
////					[SendTime]
////		FROM
////				[PhoneMessage] T WITH (NOLOCK)
////				WHERE
////		Recycled = 0 { 0 }
////		GROUP BY
////					[GroupName],
////					[Content],
////					[SendType],
////					[Remark],
////					[SendTime]
//		ActiveRecord<Object> ar = bd.ar().tableName("\t\t(SELECT\n" +
//						"\t\t\t\t[GroupName],\n" +
//						"\t\t\t\t\t[Content],\n" +
//						"\t\t\t\t\t[SendType],\n" +
//						"\t\t\t\t\t[Remark],\n" +
//						"\t\tCOUNT (0) AS GroupCount,\n" +
//						"\t\t\t\t\t[SendTime]\n" +
//						"\t\tFROM\n" +
//						"\t\t\t\t[PhoneMessage] T WITH (NOLOCK) ) as tab")
//				.eq("Recycled", 0)
//				.ne("GroupName", "aaaaa")
//				.groupBy("[GroupName]",
//						"[Content]",
//						"[SendType]",
//						"[Remark]",
//						"[SendTime]")
//				.limit(10, 20)
//				;
//		System.out.println(ar.getSql().getCondition().toString());
//		System.out.println(ar.findAll());
//	}

    @Test
    void notIn() {
        SqlPocket sql = bd.ar().notIn("id", sql("select 1 ")).getSql();
        assertEquals(" where id not in (select 1 )", sql.getCondition().toString());
        assertEquals(0, sql.getParams().size());
    }

    @Test
    void inNull() {
        SqlPocket sql = bd.ar().in("id", Optional.empty()).getSql();
        assertEquals("", sql.getCondition().toString());
        assertEquals(0, sql.getParams().size());
    }

    @Test
    void interceptorDemo(){
        //case 1
        SqlPocket basic = bd.ar().filterInterceptor(sqlHelper -> sqlHelper.eq("id", 123))
                .paramsInterceptor(list -> list).getSql();

        assertEquals(" where id = ?", basic.getCondition().toString());
        assertEquals(1, basic.getParams().size());

        //case 2
        SqlPocket  useFul= bd.ar().filterInterceptor(sql    -> new SqlHelper(sql){
                        @Override
                        public SqlPocket toCondition() {
                            return new SqlPocket(){
                                @Override
                                public StringBuffer getCondition() {
                                    return new StringBuffer(" where id = ?");
                                }
                                @Override public List<Object> getParams() {
                                    return List.of(123);
                                }
                            };
                        }
                })
                .getSql();

        assertEquals(" where id = ?", useFul.getCondition().toString());
        assertEquals(1, useFul.getParams().size());
    }


    @Test
    void findAllWithCount() {
        bd.getDbHelper().executeNoQuery(
                "delete from test1 where 1=1");
        //生成100条数据
        for (int i = 0; i < 100; i++) {
            bd.getDbHelper().executeNoQuery(
                    "insert into test(id,name)values(" + i + ",'bacddd"+i+"')");
        }
        Pair<Long, List<Object>> id = bd.ar()
                .gt("id", 0)
                .limit(10, 20)
                .findAllWithCount();
        assertEquals(101, id.getLeft());
        assertEquals(20, id.getRight().size());
    }

    public static class T {
        Long id;
        String name;
        Timestamp createtime;

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Timestamp getCreatetime() {
            return createtime;
        }

        public void setCreatetime(Timestamp createtime) {
            this.createtime = createtime;
        }

        public String toString() {
            return id + " " + name;
        }
    }

    static class BD extends AbstractBaseDao {
        public BD(IDbHelper db) {
            super(db);
        }

        @Override
        public T mapRow(ResultSet rs, int index, Set<String> set) throws SQLException {
            T t = new T();
            t.id = rs.getLong("id");
            t.name = rs.getString("name");
            if (set!=null && set.contains("createtime"))
                t.createtime = rs.getTimestamp("createtime");
            return t;
        }

        @Override
        public String getTableName() {
            return "test";
        }
    }

}
