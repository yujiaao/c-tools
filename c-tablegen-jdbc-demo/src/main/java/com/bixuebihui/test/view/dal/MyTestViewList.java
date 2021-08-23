package com.bixuebihui.test.view.dal;

/*
 *  MyTestViewList
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
import com.bixuebihui.test.view.business.*;
import com.bixuebihui.test.view.pojo.*;
import com.bixuebihui.jdbc.RowMapperResultReader;
import com.bixuebihui.DbException;

import com.bixuebihui.jdbc.AbstractViewBaseDao;

import javax.annotation.processing.Generated;

@Generated("com.github.yujiaao:tablegen")
public class MyTestViewList  extends AbstractViewBaseDao<MyTestView, Integer>
{
    
    /**
      * Don't direct use the MyTestViewList, use MyTestViewManager instead.
      */
    protected MyTestViewList(DataSource ds)
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
        public static final String DEGREE = "degree";
        
        public static String[] getAllFields() {
            return new String[] { ID, NAME, AGE, BIRTH, EDU_ID, DEGREE };
        }
    }

    
    /**
     * Get table name.
     */
    @Override
    public String getTableName()
    {

      return "( select test_gen.*, degree from test_gen left join t_edu on test_gen.edu_id=t_edu.id) as my_test_view";


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
      public MyTestView mapRow (ResultSet r, int index) throws SQLException
      {
            MyTestView res = new MyTestView();
            res.setId(r.getInt(F.ID));
            res.setName(r.getString(F.NAME));
            res.setAge(r.getShort(F.AGE));
            res.setBirth(r.getTimestamp(F.BIRTH));
            res.setEduId(r.getInt(F.EDU_ID));
            res.setDegree(r.getString(F.DEGREE));
            return res;
      }



    @Override
    public Integer getId(MyTestView info) {
        return info.getId();
    }

    @Override
    public void setId(MyTestView info, Integer id) {
    
        //no key to set, don't use this method!
    }

    @Override
    public void setIdLong(MyTestView info, long id) {
    
        //no key to set, don't use this method!
    
    }










}
