package com.bixuebihui.test.dal;

/*
 *  TEduList
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2021
 * email: www@qsn.so
 */


import java.sql.*;
import java.time.*;
import java.util.List;
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
public class TEduList  extends BaseList<TEdu, Integer>
{
    
    /**
      * Don't direct use the TEduList, use TEduManager instead.
      */

    protected TEduList(ConnectionFactory ds)
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
    }

    
    protected String getDeleteSql(){
        return "delete from "+this.getTableName()+" where  id=? ";
    }

   @Override
   protected String getInsertSql(){
       return "insert into "+this.getTableName()+" ( degree ) values (  ?  )";
   }


    @Override
    protected String getUpdateSql(){
    return "update "+this.getTableName()+" set   degree=? where id=?";
    }

    
   @Override
   protected Object[] getInsertObjs(TEdu info){
        return new Object[]{ info.getDegree() };
   }

   @Override
   protected Object[] getUpdateObjs(TEdu  info){
      return new Object[]{ info.getDegree(),info.getId() };
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
        return  F.ID;
    }


      /**
        * Updates the object from a selected ResultSet.
        */
      @Override
      public TEdu mapRow(Row r) 
      {
            TEdu res = new TEdu();
            res.setId(r.get(F.ID, Integer.class));
            res.setDegree(r.get(F.DEGREE, String.class));
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
    public Mono<Void> setIdLong(TEdu info, Mono<Long> id) {
    
            return id.flatMap(it ->{ info.setId((int)it.longValue()); return Mono.empty();});
    
    }



      
      
      

    

    
    /**
     * Deletes from the database for table t_edu with connection
     */
    @Override
    public Mono<Boolean> deleteByKey(  Integer id,   Connection cn )
    {
        return dbHelper.executeNoQuery(getDeleteSql(),
          new Object[]{  id   },
          cn).map(x -> x>=1);
    }

    /**
    * Deletes from the database for table t_edu
    */
    @Override
    public Mono<Boolean> deleteByKey(  Integer id )
    {
        return dbHelper.executeNoQuery(getDeleteSql(),
          new Object[]{  id   }).map(x -> x>=1);
    }








    /**
     * Inserts the dummy record of TEdu object values into the database.
     */
    @Override
    public Mono<Boolean> insertDummy()
    {
        TEdu  info = new TEdu();
        java.util.Random rnd = new java.util.Random();
    
        info.setDegree(Integer.toString(Math.abs(rnd.nextInt(Integer.MAX_VALUE)), 36));
    
        return getNextKey().flatMap(id -> {
        setId(info, id);
        return Mono.just(info);
        }).flatMap(dd ->  this.insert(dd));
    

    }

}
