package com.bixuebihui.test.dal;

/*
 *  TEduList
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
public class TEduList  extends BaseList<TEdu, Integer>
{
    
    /**
      * Don't direct use the TEduList, use TEduManager instead.
      */

    protected TEduList(DataSource ds)
    {
        super(ds);
    }


    
    @SuppressWarnings("AlibabaClassNamingShouldBeCamel")
    public static final class F{
        public static final String ID = "id";
        public static final String DEGREE = "degree";
        
        public static String[] getAllFields() {
            return new String[] { ID, DEGREE };
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
" ( degree)"
    +" values ("
    +"?)";
   }

    @Override
    protected String getUpdateSql(){
        return "update "+this.getTableName()+" set "
    +"degree=? where id=?";
    }

    
   @Override
   protected Object[] getInsertObjs(TEdu info){
        return new Object[]{ info.getDegree() };
   }

   @Override
   protected Object[] getUpdateObjs(TEdu  info){
      return new Object[]{ info.getDegree(),info.getId() };
   }

    @Override
    protected Object[] getUpdateObjs(TEdu  info, Set<String> excludeFields){
        if(excludeFields==null || excludeFields.isEmpty()){
            return getUpdateObjs(info);
        }
        List<Object> list = new java.util.ArrayList<>();
        Set<String> localExcludeFields = excludeFields.stream()
            .map(x -> getFieldAlias(x.toLowerCase(Locale.ROOT))).collect(Collectors.toSet());

    if(!containsIgnoreCase(F.DEGREE, localExcludeFields)){
        list.add(info.getDegree());
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

      return  "t_edu";

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
      public TEdu mapRow (ResultSet r, int index, Set<String> fields) throws SQLException
      {
            TEdu res = new TEdu();
            if (containsIgnoreCase(F.ID, fields)){
                  res.setId(r.getInt(F.ID));
            }
            if (containsIgnoreCase(F.DEGREE, fields)){
                  res.setDegree(r.getString(F.DEGREE));
            }
            return res;
      }



    @Override
    public Integer getId(TEdu info) {
    
        
        return info.getId();
    }

    @Override
    public void setId(TEdu info, Integer id) {
    
        
        info.setId(id);
    }

    @Override
    public void setIdLong(TEdu info, long id) {
    
        info.setId((int)id);
    }



  
  
      
      
  
  

    

    
    /**
     *21 Deletes from the database for table t_edu with connection
     */
    @Override
    public boolean deleteByKey(  Integer id,   Connection cn )
    {
        return 1 <= dbHelper.executeNoQuery(getDeleteSql(), new Object[]{  id   }, cn);
    }

    /**
    *22 Deletes from the database for table t_edu */
    @Override
    public boolean deleteByKey(  Integer id )
    {
        return 1 <= dbHelper.executeNoQuery(getDeleteSql(), new Object[]{  id   });
    }





    




    /**
     * Inserts the dummy record of TEdu object values into the database.
     */
    @Override
    public boolean insertDummy()
    {
        TEdu  info = new TEdu();
        java.util.Random rnd = new java.util.Random();
    
        info.setDegree(Integer.toString(Math.abs(rnd.nextInt(Integer.MAX_VALUE)), 36));
    
        info.setId(getNextKey());
    
        return this.insert(info);
    }

}
