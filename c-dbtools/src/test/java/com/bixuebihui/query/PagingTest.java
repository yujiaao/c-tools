package com.bixuebihui.query;

import com.bixuebihui.datasource.H2DataSource;
import com.bixuebihui.db.ActiveRecordImpl;
import com.bixuebihui.db.SqlPocket;
import com.bixuebihui.jdbc.AbstractViewBaseDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PagingTest  {

    static class MyAr extends  AbstractViewBaseDao<User, Integer> {
        MyAr(DataSource cp){
            super(cp);
        }

        @Override
        public User mapRow(ResultSet rs, int index, Set<String> fields) {
            return null;
        }

        @Override
        public Integer getId(User info) {
            return info.id;
        }

        @Override
        public String getTableName() {
            return "user";
        }

        @Override
        public String getKeyName() {
            return "id";
        }
    }

    @Test
    public void testToActiveRecordParams() throws SQLException {
        Paging<User> p = new Paging<>();
        p.searchBean =  new User();

        H2DataSource db = new H2DataSource().init();
        ActiveRecordImpl<User, Integer> ar = new ActiveRecordImpl<>(new MyAr(db.cp));

        Set<String> fields = Stream.of("id", "name","age", "modifiedTime").map(String::toLowerCase).collect(Collectors.toSet());
        SqlPocket res;
        ar = (ActiveRecordImpl<User, Integer>) p.toActiveRecordParams(ar, fields);

        res = ar.getCondStack().toCondition();

        assertEquals("", res.getCondition().toString());
        ar.clear();

        p.searchBean.id=1;


        ar = (ActiveRecordImpl<User, Integer>) p.toActiveRecordParams(ar, fields);
        res = ar.getCondStack().toCondition();
        assertEquals(" where id = ?", res.getCondition().toString());
        ar.clear();

        p.searchBean.begin_ModifiedTime= new Timestamp(System.currentTimeMillis());
        ar = (ActiveRecordImpl<User, Integer>) p.toActiveRecordParams(ar, fields);
        res = ar.getCondStack().toCondition();
        assertEquals(" where id = ? and modifiedtime >= ?", res.getCondition().toString());
        ar.clear();

        p.searchBean.end_ModifiedTime= new Timestamp(System.currentTimeMillis());

        ar = (ActiveRecordImpl<User, Integer>) p.toActiveRecordParams(ar, fields);
        res = ar.getCondStack().toCondition();
        assertEquals(" where id = ? and modifiedtime between ? and ?", res.getCondition().toString());
        ar.clear();

        p.searchBean.end_age= 100;

        ar = (ActiveRecordImpl<User, Integer>) p.toActiveRecordParams(ar, fields);
        res = ar.getCondStack().toCondition();
        assertEquals(" where id = ? and modifiedtime between ? and ? and age <= ?", res.getCondition().toString());
        ar.clear();


        p.searchBean.name= "xwx";
        p.searchBean.end_age= null;
        p.searchBean.begin_ModifiedTime= null;
        p.searchBean.end_ModifiedTime= null;

        ar = (ActiveRecordImpl<User, Integer>) p.toActiveRecordParams(ar, fields);
        res = ar.getCondStack().toCondition();
        assertEquals(" where name = ? and id = ?", res.getCondition().toString());
        ar.clear();

        p.searchBean.name= "xwx*";
        p.searchBean.end_age= null;
        p.searchBean.begin_ModifiedTime= null;
        p.searchBean.end_ModifiedTime= null;

        ar = (ActiveRecordImpl<User, Integer>) p.toActiveRecordParams(ar, fields);
        res = ar.getCondStack().toCondition();
        assertEquals(" where name like concat(?,'%') and id = ?", res.getCondition().toString());
        ar.clear();


        p.searchBean.begin_name= "a";
        p.searchBean.end_name= "z";
        p.searchBean.name= null;
        p.searchBean.id = null;
        p.searchBean.begin_ModifiedTime= null;
        p.searchBean.end_ModifiedTime= null;

        ar = (ActiveRecordImpl<User, Integer>) p.toActiveRecordParams(ar, fields);
        res = ar.getCondStack().toCondition();
        assertEquals(" where name between ? and ?", res.getCondition().toString());
        ar.clear();


    }

    @Test
    void sorter() throws SQLException {
        Paging<User> p = new Paging<>();
        p.searchBean =  new User();
        p.parseSort("name:asc,id:desc,modifiedTime:descend");

        assertEquals("[{property='name', order='asc'}, {property='id', order='desc'}, {property='modifiedTime', order='desc'}]", p.sort.toString());

        H2DataSource db = new H2DataSource().init();
        ActiveRecordImpl<User, Integer> ar = new ActiveRecordImpl<>(new MyAr(db.cp));

        Set<String> fields = Stream.of("id", "name","age", "modified_time").map(String::toLowerCase).collect(Collectors.toSet());

        ar = (ActiveRecordImpl<User, Integer>) p.toActiveRecordParams(ar, fields);

        String res = ar.parseOrder();

        assertEquals(" order by name asc,id desc,modified_time desc", res);

    }


    @Test
    void antiInjectionSorter() throws SQLException {
        Paging<User> p = new Paging<>();
        p.searchBean =  new User();
        p.parseSort("select 1 from duel:asc,id:desc,modifiedTime:descend");

        assertEquals("[{property='select 1 from duel', order='asc'}, {property='id', order='desc'}, {property='modifiedTime', order='desc'}]", p.sort.toString());

        H2DataSource db = new H2DataSource().init();
        ActiveRecordImpl<User, Integer> ar = new ActiveRecordImpl<>(new MyAr(db.cp));

        Set<String> fields = Stream.of("id", "name","age", "modified_time").map(String::toLowerCase).collect(Collectors.toSet());

        ar = (ActiveRecordImpl<User, Integer>) p.toActiveRecordParams(ar, fields);

        String res = ar.parseOrder();

        assertEquals(" order by id desc,modified_time desc", res);

    }


    @Test
    void dateRangeWhenDateOnly() throws SQLException {
        Paging<User> p = new Paging<>();
        p.searchBean =  new User();
        p.searchBean.begin_ModifiedTime = new Timestamp(new Date(2023-1970, 10, 1).getTime());
        p.searchBean.end_ModifiedTime = p.searchBean.begin_ModifiedTime;


        H2DataSource db = new H2DataSource().init();
        ActiveRecordImpl<User, Integer> ar = new ActiveRecordImpl<>(new MyAr(db.cp));
        Set<String> fields = Stream.of("id", "name","age", "modified_time").map(String::toLowerCase).collect(Collectors.toSet());

        ar = (ActiveRecordImpl<User, Integer>) p.toActiveRecordParams(ar, fields);
        SqlPocket condition = ar.getCondStack().toCondition();

        assertEquals(" where modified_time >= ? and modified_time < ?", condition.getCondition().toString());
        assertEquals(2, condition.getParams().size());
        assertEquals(p.searchBean.begin_ModifiedTime.getTime(), ((Date)condition.getParams().get(0)).getTime());
        assertEquals(new Date(2023-1970, 10, 2).getTime(), ((Date)condition.getParams().get(1)).getTime());

    }

    @ParameterizedTest
    @CsvSource({"users,users",
            "Field,field",  // 统一小写
            "user_s,users",  //  去下划线
            "Create_STime,createstime",  //  去下划线
            "begin_end,begin_end", // begin_ end_ 例外
            "end_date,end_date"
    })
    void unifyField(String field, String result){
        assertEquals(result, new Paging().unifyField(field));
    }
}
