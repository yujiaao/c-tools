package com.bixuebihui.jdbc;

import com.bixuebihui.DbException;
import com.bixuebihui.datasource.DataSourceTest;
import com.bixuebihui.datasource.DbcpDataSource;
import com.bixuebihui.db.ActiveRecord;
import com.bixuebihui.jdbc.aop.DbHelperAroundAdvice;
import com.bixuebihui.jdbc.event.LogAfterChangeEventHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.aop.framework.ProxyFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bixuebihui.db.Dialect.MYSQL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseDaoTest {

    public static void main(String[] args) {
        final String regex = "[(,]\\s*userid\\s*[),]";
        final String string = "insert into users ( userid, name) values(?,?)";

        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(string);

        System.out.println(matcher.matches());

        while (matcher.find()) {
            System.out.println("Full match: " + matcher.group(0));

            for (int i = 1; i <= matcher.groupCount(); i++) {
                System.out.println("Group " + i + ": " + matcher.group(i));
            }
        }
    }

    @Test
    public void testGetCount() {
        DbcpDataSource ds = new DbcpDataSource();
        ds.setDatabaseConfig(DataSourceTest.getConfig());
        IDbHelper db = new DbHelper();
        db.setDataSource(ds);
        AbstractBaseDao bd = new AbstractBaseDao(db) {

            @Override
            public String getTableName() {
                return " (select lid from t_log) t ";
            }

        };

        System.out.println(bd.addAlias(bd.getTableName()));

        System.out.println(bd.getCount(bd.getTableName(), ""));
    }

    private void derbySql() {
        DbcpDataSource ds = new DbcpDataSource();
        ds.setDatabaseConfig(DataSourceTest.getConfigDerby());
        IDbHelper db = new DbHelper();
        db.setDataSource(ds);
        AbstractBaseDao bd = new AbstractBaseDao(db) {

            @Override
            public String getTableName() {
                return "test";
            }

        };


        System.out.println(bd.getPagingSql("select * from test", 20, 40));
    }

    @Test
    public void testGetPagingSql() {
        derbySql();
    }

    @Test
    public void testFieldContains() {
        String fields = "author_id,video_id,sum(play_pv) play_pv";
        boolean res = BaseDao.containsIgnoreCase("play_pv", ActiveRecord.toFieldsSet(fields));
        assertTrue(res);

    }

    @Test
    public void testGetLastInsertID() throws SQLException {
        DbcpDataSource ds = new DbcpDataSource();
        ds.setDatabaseConfig(DataSourceTest.getConfigMaster());
        DbHelper db = new DbHelper();
        db.setDataSource(ds);
        Connection cn = db.getConnection();
        try {
            db.executeNoQuery("DROP TABLE IF EXISTS test ", cn);
            db.executeNoQuery("create table if not exists test(id int not null AUTO_INCREMENT, name varchar(100), primary key(id))",
                    cn);
        } catch (DbException ex) {
            ex.printStackTrace();
        }
        AbstractBaseDao bd = new AbstractBaseDao(db) {

            @Override
            public String getTableName() {
                return "test";
            }

        };

        bd.getDbHelper().executeNoQuery("insert into test (name)values('this is test')", null, cn);

        Long res;
        try {
            res = bd.getLastInsertId(cn);
        } catch (DbException e) {
            e.printStackTrace();
            cn.close();
            return;
        }
        assertEquals(1, res.intValue());
        cn.close();
    }

    @Test
    public void testGetUpdateSql() {
        DbcpDataSource ds = new DbcpDataSource();
        ds.setDatabaseConfig(DataSourceTest.getConfigDerby());
        IDbHelper db = new DbHelper();
        db.setDataSource(ds);
        AbstractBaseDao bd = new AbstractBaseDao(db) {

            @Override
            public String getTableName() {
                return "test";
            }

        };

        assertEquals("update test set files1=?, fieldss2=? ", bd
                .getUpdateSql(new String[]{"files1", "fieldss2"}, ""));
        assertEquals("update test set files1=? where 1=1", bd.getUpdateSql(
                new String[]{"files1"}, "where 1=1"));
    }

    @Test
    public void testUpdateBatch() {
        DbcpDataSource ds = new DbcpDataSource();
        ds.setDatabaseConfig(DataSourceTest.getConfigMaster());
        IDbHelper db = new DbHelper();
        db.setDataSource(ds);
        try {
            db.executeNoQuery("DROP TABLE if exists test");
        } catch (DbException ex) {
            ex.printStackTrace();
        }
        db.executeNoQuery("create table test(id int, name varchar(100))");
        db.executeNoQuery("insert into test(id, name) values(1,'abc')");
        db.executeNoQuery("insert into test(id, name) values(2,'abc')");
        db.executeNoQuery("insert into test(id, name) values(3,'abc')");


        class T {
            Long id;
            String name;
        }
        class BD extends BaseDao<T, Long> {
            BD(IDbHelper db) {
                super(db);
            }

            @Override
            public String getKeyName() {
                return "id";
            }

            @Override
            public T mapRow(ResultSet rs, int index, Set<String> set) throws SQLException {
                T t = new T();
                if (containsIgnoreCase("id", set)) {
                    t.id = rs.getLong("id");
                }
                if (containsIgnoreCase("name", set)) {
                    t.name = rs.getString("name");
                }
                return t;
            }

            @Override
            public boolean insertDummy() {
                return false;
            }

            @Override
            public Long getId(T info) {
                return info.id;
            }

            @Override
            public void setId(T info, Long id) {
                info.id = id;
            }

            @Override
            public String getTableName() {
                return "test";
            }

            @Override
            protected String getUpdateSql() {
                return "update " + this.getTableName() + " set name=? where id=?";
            }

            @Override
            protected Object[] getUpdateObjs(T info) {
                return new Object[]{info.name, info.id};
            }

        }

        BD bd = new BD(db);
        bd.setAfterChangeEventHandler(new LogAfterChangeEventHandler<>(bd));

        List<T> objs = bd.selectAll();
        for (T o : objs) {
            o.name = o.name + o.id;
        }

        bd.updateBatch(objs.toArray(new T[0]));

        List<T> news = bd.selectAll();
        for (T n : news) {
            assertTrue(n.name.endsWith(n.id + ""));
        }

    }

    @Test
    public void testAr() {
        DbcpDataSource ds = new DbcpDataSource();
        ds.setDatabaseConfig(DataSourceTest.getConfigDerby());
        DbHelper db = new DbHelper();
        db.setDataSource(ds);
        Connection cn = db.getConnection();

        //boolean res = JDBCUtils.tableOrViewExists(null, null, "%", cn);
        try {
            db.executeNoQuery("drop table test", cn);
            db.executeNoQuery("create table test(id int, name varchar(100))",
                    cn);
        } catch (DbException ex) {
            ex.printStackTrace();
        }
        class T {
            Long id;
            String name;
        }
        class BD extends AbstractBaseDao {
            BD(IDbHelper db) {
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

        AbstractBaseDao bd = new BD(db);
        bd.getDbHelper().executeNoQuery(
                "insert into test(id,name)values(123,'bac')");
        bd.getDbHelper().executeNoQuery(
                "insert into test(id,name)values(234,'bac')");
        Object obj = bd.ar().eq("id", 123).find();
        System.out.println(((T) obj).id);

        ActiveRecord<Object> ar = bd.ar();
        //Record<Object> ar1 =ar.eq("id", 123).clone();


        Object obj1 = ar.eq("id", 123).find();
        System.out.println(((T) obj1).id);
        int res1 = ar.last().count();
        assertEquals(1, res1);
    }

    @Test
    public void testSelectK() {
        DbcpDataSource ds = new DbcpDataSource();
        ds.setDatabaseConfig(DataSourceTest.getConfigDerby());
        DbHelper db = new DbHelper();
        db.setDataSource(ds);
        Connection cn = db.getConnection();

        try {
            db.executeNoQuery("drop table test", cn);
        } catch (Exception ex) {
        }
        try {
            db.executeNoQuery("create table test(id int, name varchar(100))",
                    cn);
            db.executeNoQuery("insert into test(id , name) values(100,'abc')",
                    cn);
            db.executeNoQuery("insert into test(id , name) values(200,'efg')",
                    cn);
            db.executeNoQuery("insert into test(id , name) values(300,'MKD')",
                    cn);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        class BD extends AbstractBaseDao {
            BD(IDbHelper db) {
                super(db);
            }
        }

        BD mybd = new BD(db);

        String select = "select id from test";
        String whereClause = " where id>100";
        String orderBy = " order by id";
        int rowStart = 0;
        int rowEnd = 100;

        List<Integer> res = mybd.select(select, whereClause, orderBy, null, rowStart, rowEnd, Integer.class);
        assertEquals(2, res.size());
        System.out.println(res);
    }

    @Test
    public void testGetSingleObject() {
        DbcpDataSource ds = new DbcpDataSource();
        ds.setDatabaseConfig(DataSourceTest.getConfigDerby());
        DbHelper db = new DbHelper();
        db.setDataSource(ds);
        Connection cn = db.getConnection();

        try {
            db.executeNoQuery("drop table test", cn);
        } catch (DbException ex) {
            //	ex.printStackTrace();
        }
        try {
            db.executeNoQuery("create table test(id int, name varchar(100), dt timestamp  default current_timestamp)",
                    cn);
            db.executeNoQuery("insert into test(id , name, dt) values(100,'abc', current_timestamp)",
                    cn);
            db.executeNoQuery("insert into test(id , name, dt) values(200,'efg', current_timestamp)",
                    cn);
            db.executeNoQuery("insert into test(id , name, dt) values(300,'MKD', null)",
                    cn);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        class BD extends AbstractBaseDao {
            BD(IDbHelper db) {
                super(db);
            }

        }

        BD mybd = new BD(db);

        String select = "select id, name, dt from test";
        String whereClause = " where id>100";
        String orderBy = " order by id";
        Simple s = new Simple();
        Simple obj = mybd.getSingleObject(select + whereClause + orderBy, new Object[]{}, s);

        assertEquals(200, obj.getId());
        assertEquals("efg", obj.getName());
        System.out.println(obj);

        List<Simple> res = mybd.select(select, whereClause, orderBy, null, 0, 100, Simple.class);
        for (Simple s1 : res) {
            System.out.println(s1);
        }

    }

    @Test
    public void testGetFirstLast() {
        DbcpDataSource ds = new DbcpDataSource();
        ds.setDatabaseConfig(DataSourceTest.getConfigDerby());
        DbHelper db = new DbHelper();
        db.setDataSource(ds);
        Connection cn = db.getConnection();

        try {
            db.executeNoQuery("drop table test1", cn);
        } catch (DbException ex) {
            //ex.printStackTrace();
        }
        try {
            db.executeNoQuery("create table test1(id int, name varchar(100), dt timestamp  default current_timestamp, primary key (id))",
                    cn);
            db.executeNoQuery("insert into test1(id , name, dt) values(300,'MKD', null)",
                    cn);
            db.executeNoQuery("insert into test1(id , name, dt) values(100,'abc', current_timestamp)",
                    cn);
            db.executeNoQuery("insert into test1(id , name, dt) values(200,'efg', current_timestamp)",
                    cn);
        } catch (DbException ex) {
            ex.printStackTrace();
        }
        class DbObj {
            Long id;
            String name;
            Timestamp dt;
        }

        class BD extends BaseDao<DbObj, Long> {
            public BD(IDbHelper db) {
                super(db);
            }

            @Override
            public DbObj mapRow(ResultSet rs, int index, Set<String> set) throws SQLException {
                DbObj o = new DbObj();
                o.id = rs.getLong(1);
                o.name = rs.getString(2);
                o.dt = rs.getTimestamp(3);
                return o;
            }

            @Override
            public String getKeyName() {
                return "ID";
            }

            @Override
            public String getTableName() {
                return "test1";
            }

            @Override
            public boolean insertDummy() {
                return false;
            }

            @Override
            public Long getId(DbObj info) {
                return info.id;
            }

            @Override
            public void setId(DbObj info, Long id) {
                info.id = id;
            }

            @Override
            public Long getNextKey() {
                return null;
            }

            @Override
            protected void setIdLong(DbObj info, long id) {
                setId(info, id);
            }

        }

        BD mybd = new BD(db);


        assertEquals(300, mybd.getLast().id);
        assertEquals(100, mybd.getFirst().id);

        assertEquals("MKD", mybd.getLast().name);
        assertEquals("abc", mybd.getFirst().name);
        System.out.println(mybd.getLast().dt);
        System.out.println(mybd.getFirst().dt);

    }

    @Test
    public void testConvert() {

        DbcpDataSource ds = new DbcpDataSource();
        ds.setDatabaseConfig(DataSourceTest.getConfigDerby());
        DbHelper db = new DbHelper();
        db.setDataSource(ds);
        ProxyFactory obj = new ProxyFactory(db);
        obj.addAdvice(new DbHelperAroundAdvice());
        IDbHelper dbHelper = (IDbHelper) obj.getProxy();

//		try{
//			dbHelper.executeNoQuery("drop table test2");
//		}catch(Exception ignored){
//		}
        try {
            dbHelper.executeNoQuery("create table test2(id bigint, name_Snake varchar(100), dt timestamp  default current_timestamp, primary key (id))");
            dbHelper.executeNoQuery("insert into test2(id , name_Snake, dt) values(300,'MKD', null)");
            dbHelper.executeNoQuery("insert into test2(id , name_Snake, dt) values(100,'abc', current_timestamp)");
            dbHelper.executeNoQuery("insert into test2(id , name_Snake, dt) values(200,'efg', current_timestamp)");
        } catch (DbException ex) {
            ex.printStackTrace();
        }

        class BD extends BaseDao<DbObj2, Long> {
            public BD(IDbHelper db) {
                super(db);
            }

            @Override
            public DbObj2 mapRow(ResultSet rs, int index, Set<String> set) throws SQLException {
                DbObj2 o = new DbObj2();
                o.id = rs.getLong(1);
                o.nameSnake = rs.getString(2);
                o.dt = rs.getTimestamp(3);
                return o;
            }

            @Override
            public String getKeyName() {
                return "ID";
            }

            @Override
            public String getTableName() {
                return "test2";
            }

            @Override
            public boolean insertDummy() {
                return false;
            }

            @Override
            public Long getId(DbObj2 info) {
                return info.id;
            }

            @Override
            public void setId(DbObj2 info, Long id) {
                info.id = id;
            }

            @Override
            public Long getNextKey() {
                return null;
            }

            @Override
            protected void setIdLong(DbObj2 info, long id) {
                setId(info, id);
            }

        }

        BD mybd = new BD(dbHelper);
        //List<DbObj2> list = mybd.ar().asc("name_Snake").findAll(DbObj2.class);
        List<DbObj2> list1 = mybd.ar().asc("name_Snake").findAll();

        assertEquals("MKD", list1.get(0).nameSnake);
        assertEquals("efg", list1.get(2).nameSnake);

        List<DbObj2> list = mybd.ar().asc("name_Snake").findAll(DbObj2.class);

        assertEquals("MKD", list.get(0).nameSnake);
        assertEquals("efg", list.get(2).nameSnake);


        System.out.println(mybd.getLast().dt);
        System.out.println(mybd.getFirst().dt);

    }

    @Test
    public void testUpsert() {
        DbcpDataSource ds = new DbcpDataSource();
        ds.setDatabaseConfig(DataSourceTest.getConfigMaster());
        IDbHelper db = new DbHelper();

        ProxyFactory obj = new ProxyFactory(db);
        obj.addAdvice(new DbHelperAroundAdvice());

        db.setDataSource(ds);
        String tableName = "test_upsert";
        try {
            db.executeNoQuery("DROP TABLE if exists " + tableName);
        } catch (DbException ex) {
            ex.printStackTrace();
        }
        db.executeNoQuery("create table " + tableName + "(id int primary key auto_increment, name varchar(100))");
        db.executeNoQuery("insert into " + tableName + "(id, name) values(1,'abc')");
        db.executeNoQuery("insert into " + tableName + "(id, name) values(2,'abc')");
        db.executeNoQuery("insert into " + tableName + "(id, name) values(3,'abc')");

        class T {
            Long id;
            String name;
        }
        class BD extends BaseDao<T, Long> {
            BD(IDbHelper db) {
                super(db);
            }

            @Override
            public String getKeyName() {
                return "id";
            }

            @Override
            public T mapRow(ResultSet rs, int index, Set<String> set) throws SQLException {
                T t = new T();
                if (containsIgnoreCase("id", set)) {
                    t.id = rs.getLong("id");
                }
                if (containsIgnoreCase("name", set)) {
                    t.name = rs.getString("name");
                }
                return t;
            }

            @Override
            public boolean insertDummy() {
                return false;
            }

            @Override
            public Long getId(T info) {
                return info.id;
            }

            @Override
            public void setId(T info, Long id) {
                info.id = id;
            }

            @Override
            public String getTableName() {
                return tableName;
            }

            @Override
            protected String getUpdateSql() {
                return "update " + this.getTableName() + " set name=? where id=?";
            }

            @Override
            protected Object[] getUpdateObjs(T info) {
                return new Object[]{info.name, info.id};
            }

            @Override
            protected Object[] getInsertObjs(T info) {
                return new Object[]{info.name};
            }

            @Override
            protected String getInsertSql() {
                return "insert into  " + this.getTableName() + " ( name ) values ( ? )";
            }

        }

        BD bd = new BD((IDbHelper) obj.getProxy());
        assertEquals(MYSQL, bd.getDbType());
        bd.setAfterChangeEventHandler(new LogAfterChangeEventHandler<>(bd));

        List<T> objs = bd.selectAll();
        for (T o : objs) {
            o.name = o.name + o.id;
        }

        T t = new T();
        t.id = 4L;
        t.name = "def" + t.id;
        objs.add(t);

        bd.upsertBatch(objs);

        List<T> news = bd.selectAll();
        assertEquals(4, news.size());
        for (T n : news) {
            assertTrue(n.name.endsWith(String.valueOf(n.id)));
        }

    }

    @ParameterizedTest
    @CsvSource({
            "'INSERT into users ( userid, name) values(?,?)','userid'",
            "'INSERT into users (asb, userid, name) values(?,?)','userid'",
            "'INSERT into users (name, userid) values(?,?)','userId'",
//			"'INSERT into users (name, userid) values(?,?)','user_Id'",
    })
    void matchField(String sql, String field) {
        assertTrue(BaseDao.matchField(sql, field));
    }

    public static class Simple {
        private String name;
        private long id;
        private Timestamp dt;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public Timestamp getDt() {
            return dt;
        }

        public void setDt(Timestamp dt) {
            this.dt = dt;
        }

        public String toString() {
            return "id=" + id + ", name=" + name + ", dt=" + dt;
        }
    }

    public static class DbObj2 {
        Long id;
        String nameSnake;
        Timestamp dt;

        public DbObj2() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNameSnake() {
            return nameSnake;
        }

        public void setNameSnake(String nameSnake) {
            this.nameSnake = nameSnake;
        }

        public Timestamp getDt() {
            return dt;
        }

        public void setDt(Timestamp dt) {
            this.dt = dt;
        }
    }

}
