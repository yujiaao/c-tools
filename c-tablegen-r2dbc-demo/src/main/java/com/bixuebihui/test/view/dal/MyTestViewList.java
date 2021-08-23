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
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.Connection;


import com.bixuebihui.test.view.business.*;
import com.bixuebihui.test.view.pojo.*;
import com.bixuebihui.DbException;

import com.bixuebihui.r2dbc.sql.AbstractViewBaseDao;

import javax.annotation.processing.Generated;
import reactor.core.publisher.Mono;

@Generated("com.github.yujiaao:tablegen")
public class MyTestViewList  extends AbstractViewBaseDao<MyTestView, Integer>
{
    
    /**
      * Don't direct use the MyTestViewList, use MyTestViewManager instead.
      */
    protected MyTestViewList(ConnectionFactory ds)
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
    * Get table view sql.
    */
    protected String getCoreSql()
    {
    
    
        return " select test_gen.*, degree from test_gen left join t_edu on test_gen.edu_id=t_edu.id";
    }

    /**
     * Get table name.
     */
    @Override
    public String getTableName()
    {

      return "("+this.getCoreSql()+") as my_test_view";
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
      public MyTestView mapRow(Row r) 
      {
            MyTestView res = new MyTestView();
            res.setId(r.get(F.ID, Integer.class));
            res.setName(r.get(F.NAME, String.class));
            res.setAge(r.get(F.AGE, Short.class));
            res.setBirth(r.get(F.BIRTH, Timestamp.class));
            res.setEduId(r.get(F.EDU_ID, Integer.class));
            res.setDegree(r.get(F.DEGREE, String.class));
            return res;
      }



    @Override
    public Integer getId(MyTestView info) {
        return info.getId();
    }











}
