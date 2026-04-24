package com.bixuebihui.test.view.dal;

/*
 *  MyTestViewList
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
import com.bixuebihui.test.view.business.*;
import com.bixuebihui.test.view.pojo.*;

import com.bixuebihui.jdbc.AbstractViewBaseDao;

import javax.annotation.processing.Generated;

import static java.util.Collections.emptySet;

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
        public static final String EDU_ID = "edu_id";
        
        public static String[] getAllFields() {
            return new String[] { EDU_ID };
        }
        public static String[] getKeyFields(){
            return new String[] {
            F.EDU_ID
            
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




    /**
    * Get table view sql.
    */
    @Override
    protected String getCoreSql()
    {
    
    
        return """
         select edu_id from test_gen left join t_edu on test_gen.edu_id=t_edu.id
        """;
    }

    protected String getSelectAllFromTable() {
        return getCoreSql();
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
    { return null;
    }


      /**
        * Updates the object from a selected ResultSet.
        */
      @Override
      public MyTestView mapRow (ResultSet r, int index, Set<String> fields) throws SQLException
      {
            MyTestView res = new MyTestView();
            if (containsIgnoreCase(F.EDU_ID, fields)){
                  res.setEduId(r.getInt(F.EDU_ID));
            }
            return res;
      }



    @Override
    public Integer getId(MyTestView info) {
    
        return null;
    }








    




}
