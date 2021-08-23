package com.bixuebihui.test.dal;

/*
 *  TestGenList
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2021
 * email: www@qsn.so
 */


import java.sql.*;
import java.util.List;
import javax.sql.DataSource;
import com.bixuebihui.test.business.*;
import com.bixuebihui.test.pojo.*;
import com.bixuebihui.jdbc.RowMapperResultReader;
import com.bixuebihui.DbException;

import com.bixuebihui.test.BaseList;

import javax.annotation.processing.Generated;

@Generated("com.github.yujiaao:tablegen")
public class TestGenList  extends BaseList<TestGen, Integer>
{
    
    /**
      * Don't direct use the TestGenList, use TestGenManager instead.
      */
    protected TestGenList(DataSource ds)
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
        
        public static String[] getAllFields() {
            return new String[] { ID, NAME, AGE, BIRTH, EDU_ID };
        }
    }

    
    protected String getDeleteSql(){
        return "delete from "+this.getTableName()+" where  id=? ";
    }

   @Override
   protected String getInsertSql(){
       return "insert into "+this.getTableName()+" ( name ,age ,birth ,edu_id ) values (  ? ,  ? ,  ? ,  ?  )";
   }


    @Override
    protected String getUpdateSql(){ 
        return "update "+this.getTableName()+" set   id=?,  name=?,  age=?,  birth=?,  edu_id=?  where id=?";
    }

    
   @Override
   protected Object[] getInsertObjs(TestGen info){
        return new Object[]{ info.getName(),info.getAge(),info.getBirth(),info.getEduId() };
   }

   @Override
   protected Object[] getUpdateObjs(TestGen  info){
      return new Object[]{ info.getName(),info.getAge(),info.getBirth(),info.getEduId(),info.getId() };
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
      public TestGen mapRow (ResultSet r, int index) throws SQLException
      {
            TestGen res = new TestGen();
            res.setId(r.getInt(F.ID));
            res.setName(r.getString(F.NAME));
            res.setAge(r.getShort(F.AGE));
            res.setBirth(r.getTimestamp(F.BIRTH));
            res.setEduId(r.getInt(F.EDU_ID));
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
    public void setIdLong(TestGen info, long id) {
    
        info.setId((int)id);
    
    }


      
      
      

    

    
    /**
     * Deletes from the database for table test_gen with connection
     */
    @Override
    public boolean deleteByKey(  Integer id,   Connection cn )
    {
        return 1 <= dbHelper.executeNoQuery(getDeleteSql(), new Object[]{  id   }, cn);
    }

    /**
    * Deletes from the database for table test_gen
    */
    @Override
    public boolean deleteByKey(  Integer id )
    {
        return 1 <= dbHelper.executeNoQuery(getDeleteSql(), new Object[]{  id   });
    }








    /**
     * Inserts the dummy record of TestGen object values into the database.
     */
    @Override
    public boolean insertDummy()
    {
        TestGen  info = new TestGen();
        java.util.Random rnd = new java.util.Random();
    info.setName(Integer.toString(Math.abs(rnd.nextInt(Integer.MAX_VALUE)), 36));
        
        info.setId(getNextKey());
    
        return this.insert(info);
    }

}
