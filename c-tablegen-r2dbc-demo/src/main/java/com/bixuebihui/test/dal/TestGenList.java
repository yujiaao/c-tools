package com.bixuebihui.test.dal;

/*
 *  TestGenList
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2022
 * email: xwx@live.cn
 */


import java.sql.*;
import java.time.*;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.Connection;


import com.bixuebihui.test.business.*;
import com.bixuebihui.test.pojo.*;
import com.bixuebihui.DbException;

import com.bixuebihui.test.BaseList;

import javax.annotation.processing.Generated;
import reactor.core.publisher.Mono;

@Generated("com.github.yujiaao:tablegen")
public class TestGenList  extends BaseList<TestGen, Integer>
{
    
    /**
      * Don't direct use the TestGenList, use TestGenManager instead.
      */

    protected TestGenList(ConnectionFactory ds)
    {
        super(ds);
    }
    
    @SuppressWarnings("AlibabaClassNamingShouldBeCamel")
    public static final class F{
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String AGE = "age";
        public static final String BIRTH = "birth";
        public static final String EDU_ID = "edu_id";
        public static final String GENDER = "gender";
        public static final String SEX = "sex";
        public static final String CHILD = "child";
        
        public static String[] getAllFields() {
            return new String[] { ID, NAME, AGE, BIRTH, EDU_ID, GENDER, SEX, CHILD };
        }
    }

    @Override
    public List<String> getAllFields(){
        return Arrays.asList(F.getAllFields());
    }


    
    protected String getDeleteSql(){
        return "delete from "+this.getTableName()+" where  id=? ";
    }

   @Override
   protected String getInsertSql(){
       return "insert into "+this.getTableName()+" ( name ,age ,birth ,edu_id ,gender ,sex ,child ) values (  ? ,  ? ,  ? ,  ? ,  ? ,  ? ,  ?  )";
   }


    @Override
    protected String getUpdateSql(){
    return "update "+this.getTableName()+" set   name=?,  age=?,  birth=?,  edu_id=?,  gender=?,  sex=?,  child=? where id=?";
    }

    
   @Override
   protected Object[] getInsertObjs(TestGen info){
        return new Object[]{ info.getName(),info.getAge(),info.getBirth(),info.getEduId(),info.getGender(),info.getSex(),info.getChild() };
   }

   @Override
   protected Object[] getUpdateObjs(TestGen  info){
      return new Object[]{ info.getName(),info.getAge(),info.getBirth(),info.getEduId(),info.getGender(),info.getSex(),info.getChild(),info.getId() };
   }



    /**
     * Get table name.
     */
    @Override
    public String getTableName()
    {
return  "test_gen";

    }

    
    /**
    * Get key name.
    */
    @Override
    public String getKeyName()
    {
        return  F.ID;
    }


      /**
        * Updates the object from a selected ResultSet.
        */
      @Override
      public TestGen mapRow(Row r) 
      {
            TestGen res = new TestGen();
            res.setId(r.get(F.ID, Integer.class));
            res.setName(r.get(F.NAME, String.class));
            res.setAge(r.get(F.AGE, Short.class));
            res.setBirth(r.get(F.BIRTH, LocalDateTime.class));
            res.setEduId(r.get(F.EDU_ID, Integer.class));
            res.setGender(r.get(F.GENDER, String.class));
            res.setSex(r.get(F.SEX, Boolean.class));
            res.setChild(r.get(F.CHILD, Boolean.class));
            return res;
      }



    @Override
    public Integer getId(TestGen info) {
        return info.getId();
    }

    @Override
    public void setId(TestGen info, Integer id) {
    
        info.setId(id);
    }

    @Override
    public Mono<Void> setIdLong(TestGen info, Mono<Long> id) {
    
            return id.flatMap(it ->{ info.setId((int)it.longValue()); return Mono.empty();});
    
    }



      
      
      

    

    
    /**
     * Deletes from the database for table test_gen with connection
     */
    @Override
    public Mono<Boolean> deleteByKey(  Integer id,   Connection cn )
    {
        return dbHelper.executeNoQuery(getDeleteSql(),
          new Object[]{  id   },
          cn).map(x -> x>=1);
    }

    /**
    * Deletes from the database for table test_gen
    */
    @Override
    public Mono<Boolean> deleteByKey(  Integer id )
    {
        return dbHelper.executeNoQuery(getDeleteSql(),
          new Object[]{  id   }).map(x -> x>=1);
    }








    /**
     * Inserts the dummy record of TestGen object values into the database.
     */
    @Override
    public Mono<Boolean> insertDummy()
    {
        TestGen  info = new TestGen();
        java.util.Random rnd = new java.util.Random();
    
        info.setName(Integer.toString(Math.abs(rnd.nextInt(Integer.MAX_VALUE)), 36));
    
        info.setGender(Integer.toString(Math.abs(rnd.nextInt(Integer.MAX_VALUE)), 36));
    
        return getNextKey().flatMap(id -> {
        setId(info, id);
        return Mono.just(info);
        }).flatMap(dd ->  this.insert(dd));
    

    }

}
