package com.bixuebihui.test.dal;

/*
 *  TestGenList
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2023
 * email: xwx@live.cn
 */


import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Locale;
import javax.sql.DataSource;
import com.bixuebihui.jdbc.RowMapperResultReader;
import com.bixuebihui.test.business.*;
import com.bixuebihui.test.pojo.*;

import com.bixuebihui.test.BaseList;

import javax.annotation.processing.Generated;

import static java.util.Collections.emptySet;

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
        public static final String GENDER = "gender";
        public static final String SEX = "sex";
        public static final String CHILD = "child";
        
        public static String[] getAllFields() {
            return new String[] { ID, NAME, AGE, BIRTH, EDU_ID, GENDER, SEX, CHILD };
        }
        public static String[] getKeyFields(){
            return new String[] {
            F.ID
            
            };
        }
    }

    @Override
    public List<String> getAllFields(){
        return Arrays.asList(F.getAllFields());
    }

    @Override
    public List<String> getKeyFields(){
        return Arrays.asList(F.getKeyFields());
    }


    
    protected String getDeleteSql(){
        return "delete from "+this.getTableName()+" where  id=? ";
    }

   @Override
   protected String getInsertSql(){
       return "insert into "+this.getTableName()+
" ( name, age, birth, edu_id, gender, sex, child)"
    +" values ("
    +"?,?,?,?,?,?,?)";
   }

    @Override
    protected String getUpdateSql(){
        return "update "+this.getTableName()+" set "
    +"name=?,age=?,birth=?,edu_id=?,gender=?,sex=?,child=? where id=?";
    }

    
   @Override
   protected Object[] getInsertObjs(TestGen info){
        return new Object[]{ info.getName(),info.getAge(),info.getBirth(),info.getEduId(),info.getGender(),info.getSex(),info.getChild() };
   }

   @Override
   protected Object[] getUpdateObjs(TestGen  info){
      return new Object[]{ info.getName(),info.getAge(),info.getBirth(),info.getEduId(),info.getGender(),info.getSex(),info.getChild(),info.getId() };
   }

    @Override
    protected Object[] getUpdateObjs(TestGen  info, Set<String> excludeFields){
        if(excludeFields==null || excludeFields.isEmpty()){
            return getUpdateObjs(info);
        }
        List<Object> list = new java.util.ArrayList<>();
        Set<String> localExcludeFields = excludeFields.stream()
            .map(x -> getFieldAlias(x.toLowerCase(Locale.ROOT))).collect(Collectors.toSet());

    if(!containsIgnoreCase(F.NAME, localExcludeFields)){
        list.add(info.getName());
    }
    if(!containsIgnoreCase(F.AGE, localExcludeFields)){
        list.add(info.getAge());
    }
    if(!containsIgnoreCase(F.BIRTH, localExcludeFields)){
        list.add(info.getBirth());
    }
    if(!containsIgnoreCase(F.EDU_ID, localExcludeFields)){
        list.add(info.getEduId());
    }
    if(!containsIgnoreCase(F.GENDER, localExcludeFields)){
        list.add(info.getGender());
    }
    if(!containsIgnoreCase(F.SEX, localExcludeFields)){
        list.add(info.getSex());
    }
    if(!containsIgnoreCase(F.CHILD, localExcludeFields)){
        list.add(info.getChild());
    }
  list.add(info.getId());

        return list.toArray();
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
        return  F.ID ;
    }


      /**
        * Updates the object from a selected ResultSet.
        */
      @Override
      public TestGen mapRow (ResultSet r, int index, Set<String> fields) throws SQLException
      {
            TestGen res = new TestGen();
            if (containsIgnoreCase(F.ID, fields)){
                  res.setId(r.getInt(F.ID));
            }
            if (containsIgnoreCase(F.NAME, fields)){
                  res.setName(r.getString(F.NAME));
            }
            if (containsIgnoreCase(F.AGE, fields)){
                  res.setAge(r.getShort(F.AGE));
            }
            if (containsIgnoreCase(F.BIRTH, fields)){
                  res.setBirth(r.getTimestamp(F.BIRTH));
            }
            if (containsIgnoreCase(F.EDU_ID, fields)){
                  res.setEduId(r.getInt(F.EDU_ID));
            }
            if (containsIgnoreCase(F.GENDER, fields)){
                  res.setGender(r.getString(F.GENDER));
            }
            if (containsIgnoreCase(F.SEX, fields)){
                  res.setSex(r.getBoolean(F.SEX));
            }
            if (containsIgnoreCase(F.CHILD, fields)){
                  res.setChild(r.getBoolean(F.CHILD));
            }
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
  * Select from the database for table "test_gen"
 */
public TestGen selectByIndTestGen(Integer edu_id,String name) throws SQLException
{
    String query = "select * from " + getTableName() + " where edu_id=? and name=?";
    List<TestGen> info = dbHelper.executeQuery(query,  new Object[]{ edu_id , name}, new RowMapperResultReader<TestGen>(this));
    if(info!=null && info.size()>0) {
         return  info.get(0);
    }
 return null;
}

/**
  * Select from the database for table "TestGen"
  */
public List<TestGen> selectAllLikeIndTestGen(Integer edu_id,String name) throws SQLException
{
    String query = "select * from " + getTableName() + " where edu_id=? and name like ?";
    return dbHelper.executeQuery(query,  new Object[]{ edu_id , "%"+name+"%"}, new RowMapperResultReader<TestGen>(this));
}

/**
  * Updates the current object values into the database with version condition as an optimistic database lock.
  */
public boolean updateByIndTestGen(TestGen info) throws SQLException
{
    String updateSql = getUpdateSql()+" and version=?";
    return 1 == dbHelper.executeNoQuery(updateSql, new Object[]{ info.getId(), info.getName(), info.getAge(), info.getBirth(), info.getEduId(), info.getGender(), info.getSex(), info.getChild(),
	 info.getEduId() , info.getName()});
}

/**
  * Updates the current object values into the database with version condition as an optimistic database lock.
  */
public boolean updateByIndTestGen(TestGen info, Connection cn) throws SQLException
{
    String updateSql = getUpdateSql()+" and version=?";
    return 1 == dbHelper.executeNoQuery(updateSql, new Object[]{ info.getId(), info.getName(), info.getAge(), info.getBirth(), info.getEduId(), info.getGender(), info.getSex(), info.getChild(),
	 info.getEduId() , info.getName()}, cn);
}

/**
  * Deletes from the database for table "test_gen"
  */
public boolean deleteByIndTestGen(Integer edu_id,String name) throws SQLException
{

    return 1 <= dbHelper.executeNoQuery(getDeleteSql(), new Object[]{ edu_id , name});
}

/**
  * Deletes from the database for table "test_gen"
  */
public boolean deleteByIndTestGen(Integer edu_id,String name, Connection cn) throws SQLException
{

    return 1 <= dbHelper.executeNoQuery(getDeleteSql(), new Object[]{ edu_id , name}, cn);
}

/**
  * Counts the number of entries for this table in the database.
  */
public int countByIndTestGen(Integer edu_id,String name) throws SQLException
{
    String query="select count(*) from " + getTableName() + "   where edu_id=? and name=?";
    Object o = dbHelper.executeScalar(query, new Object[]{ edu_id , name});
  return o==null?0:Integer.parseInt(o.toString());
}

/**
  * Counts the number of entries for this table in the database.
  */
public int countLikeIndTestGen(Integer edu_id,String name) throws SQLException
{
    String query="select count(*) from " + getTableName() + "   where edu_id=? and name like ?";
    Object o = dbHelper.executeScalar(query, new Object[]{ edu_id , "%"+name+"%"});
  return o==null?0:Integer.parseInt(o.toString());
}

/**
  * Select from the database for table "test_gen"
 */
public TestGen selectByTEduId(Integer edu_id) throws SQLException
{
    String query = "select * from " + getTableName() + " where edu_id=?";
    List<TestGen> info = dbHelper.executeQuery(query,  new Object[]{ edu_id}, new RowMapperResultReader<TestGen>(this));
    if(info!=null && info.size()>0) {
         return  info.get(0);
    }
 return null;
}

/**
  * Select from the database for table "TestGen"
  */
public List<TestGen> selectAllLikeTEduId(Integer edu_id) throws SQLException
{
    String query = "select * from " + getTableName() + " where edu_id=?";
    return dbHelper.executeQuery(query,  new Object[]{ edu_id}, new RowMapperResultReader<TestGen>(this));
}

/**
  * Updates the current object values into the database with version condition as an optimistic database lock.
  */
public boolean updateByTEduId(TestGen info) throws SQLException
{
    String updateSql = getUpdateSql()+" and version=?";
    return 1 == dbHelper.executeNoQuery(updateSql, new Object[]{ info.getId(), info.getName(), info.getAge(), info.getBirth(), info.getEduId(), info.getGender(), info.getSex(), info.getChild(),
	 info.getEduId()});
}

/**
  * Updates the current object values into the database with version condition as an optimistic database lock.
  */
public boolean updateByTEduId(TestGen info, Connection cn) throws SQLException
{
    String updateSql = getUpdateSql()+" and version=?";
    return 1 == dbHelper.executeNoQuery(updateSql, new Object[]{ info.getId(), info.getName(), info.getAge(), info.getBirth(), info.getEduId(), info.getGender(), info.getSex(), info.getChild(),
	 info.getEduId()}, cn);
}

/**
  * Deletes from the database for table "test_gen"
  */
public boolean deleteByTEduId(Integer edu_id) throws SQLException
{

    return 1 <= dbHelper.executeNoQuery(getDeleteSql(), new Object[]{ edu_id});
}

/**
  * Deletes from the database for table "test_gen"
  */
public boolean deleteByTEduId(Integer edu_id, Connection cn) throws SQLException
{

    return 1 <= dbHelper.executeNoQuery(getDeleteSql(), new Object[]{ edu_id}, cn);
}

/**
  * Counts the number of entries for this table in the database.
  */
public int countByTEduId(Integer edu_id) throws SQLException
{
    String query="select count(*) from " + getTableName() + "   where edu_id=?";
    Object o = dbHelper.executeScalar(query, new Object[]{ edu_id});
  return o==null?0:Integer.parseInt(o.toString());
}

/**
  * Counts the number of entries for this table in the database.
  */
public int countLikeTEduId(Integer edu_id) throws SQLException
{
    String query="select count(*) from " + getTableName() + "   where edu_id=?";
    Object o = dbHelper.executeScalar(query, new Object[]{ edu_id});
  return o==null?0:Integer.parseInt(o.toString());
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
    
        info.setGender(Integer.toString(Math.abs(rnd.nextInt(Integer.MAX_VALUE)), 36));
    
        info.setId(getNextKey());
    
        return this.insert(info);
    }

}
