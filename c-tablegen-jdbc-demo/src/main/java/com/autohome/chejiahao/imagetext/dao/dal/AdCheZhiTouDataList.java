package com.autohome.chejiahao.imagetext.dao.dal;

/*
 *  AdCheZhiTouDataList
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2022
 * email: xwx@live.cn
 */


import java.sql.*;
import java.util.Set;
import javax.sql.DataSource;
import com.autohome.chejiahao.imagetext.dao.business.*;
import com.autohome.chejiahao.imagetext.dao.pojo.*;
import com.bixuebihui.jdbc.RowMapperResultReader;
import com.bixuebihui.DbException;

import com.autohome.chejiahao.imagetext.dao.BaseList;

import javax.annotation.processing.Generated;

@Generated("com.github.yujiaao:tablegen")
public class AdCheZhiTouDataList  extends BaseList<AdCheZhiTouData, Integer> 
{
    
    /**
      * Don't direct use the AdCheZhiTouDataList, use AdCheZhiTouDataManager instead.
      */

    protected AdCheZhiTouDataList(DataSource readWrite, DataSource readOnly)
    {
        super(readWrite,readOnly);
    }


    
    @SuppressWarnings("AlibabaClassNamingShouldBeCamel")
    public static final class F{
        public static final String ID = "Id";
        public static final String CURRENTDATE = "CurrentDate";
        public static final String ADID = "AdId";
        public static final String SHOWCOUNT = "ShowCount";
        public static final String CLICKCOUNT = "ClickCount";
        public static final String INCOME = "InCome";
        public static final String CREATETIME = "CreateTime";
        public static final String REALINCOME = "RealInCome";
        public static final String RV = "RV";
        public static final String REASON = "Reason";
        public static final String ADJUSTPERCENT = "AdjustPercent";
        public static final String ADJUSTVALUE = "AdjustValue";
        
        public static String[] getAllFields() {
            return new String[] { ID, CURRENTDATE, ADID, SHOWCOUNT, CLICKCOUNT, INCOME, CREATETIME, REALINCOME, RV, REASON, ADJUSTPERCENT, ADJUSTVALUE };
        }
    }

    
    protected String getDeleteSql(){
        return "delete from "+this.getTableName()+" where  Id=? ";
    }

   @Override
   protected String getInsertSql(){
       return "insert into "+this.getTableName()+" ( CurrentDate ,AdId ,ShowCount ,ClickCount ,InCome ,CreateTime ,RealInCome ,RV ,Reason ,AdjustPercent ,AdjustValue ) values (  ? ,  ? ,  ? ,  ? ,  ? ,  ? ,  ? ,  ? ,  ? ,  ? ,  ?  )";
   }


    @Override
    protected String getUpdateSql(){ 
        return "update "+this.getTableName()+" set   CurrentDate=?,  AdId=?,  ShowCount=?,  ClickCount=?,  InCome=?,  CreateTime=?,  RealInCome=?,  RV=?,  Reason=?,  AdjustPercent=?,  AdjustValue=? where Id=?";
    }

    
   @Override
   protected Object[] getInsertObjs(AdCheZhiTouData info){
        return new Object[]{ info.getCurrentDate(),info.getAdId(),info.getShowCount(),info.getClickCount(),info.getInCome(),info.getCreateTime(),info.getRealInCome(),info.getRV(),info.getReason(),info.getAdjustPercent(),info.getAdjustValue() };
   }

   @Override
   protected Object[] getUpdateObjs(AdCheZhiTouData  info){
      return new Object[]{ info.getCurrentDate(),info.getAdId(),info.getShowCount(),info.getClickCount(),info.getInCome(),info.getCreateTime(),info.getRealInCome(),info.getRV(),info.getReason(),info.getAdjustPercent(),info.getAdjustValue(),info.getId() };
   }



    /**
     * Get table name.
     */
    @Override
    public String getTableName()
    {
return  "AdCheZhiTouData";

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
      public AdCheZhiTouData mapRow (ResultSet r, int index, Set<String> fields) throws SQLException
      {
            AdCheZhiTouData res = new AdCheZhiTouData();
            if (containsIgnoreCase(F.ID, fields)){
                  res.setId(r.getInt(F.ID));
            }
            if (containsIgnoreCase(F.CURRENTDATE, fields)){
                  res.setCurrentDate(r.getTimestamp(F.CURRENTDATE));
            }
            if (containsIgnoreCase(F.ADID, fields)){
                  res.setAdId(r.getInt(F.ADID));
            }
            if (containsIgnoreCase(F.SHOWCOUNT, fields)){
                  res.setShowCount(r.getLong(F.SHOWCOUNT));
            }
            if (containsIgnoreCase(F.CLICKCOUNT, fields)){
                  res.setClickCount(r.getLong(F.CLICKCOUNT));
            }
            if (containsIgnoreCase(F.INCOME, fields)){
                  res.setInCome(r.getDouble(F.INCOME));
            }
            if (containsIgnoreCase(F.CREATETIME, fields)){
                  res.setCreateTime(r.getTimestamp(F.CREATETIME));
            }
            if (containsIgnoreCase(F.REALINCOME, fields)){
                  res.setRealInCome(r.getDouble(F.REALINCOME));
            }
            if (containsIgnoreCase(F.RV, fields)){
                  res.setRV(r.getBytes(F.RV));
            }
            if (containsIgnoreCase(F.REASON, fields)){
                  res.setReason(r.getString(F.REASON));
            }
            if (containsIgnoreCase(F.ADJUSTPERCENT, fields)){
                  res.setAdjustPercent(r.getDouble(F.ADJUSTPERCENT));
            }
            if (containsIgnoreCase(F.ADJUSTVALUE, fields)){
                  res.setAdjustValue(r.getDouble(F.ADJUSTVALUE));
            }
            return res;
      }



    @Override
    public Integer getId(AdCheZhiTouData info) {
        return info.getId();
    }

    @Override
    public void setId(AdCheZhiTouData info, Integer id) {
    
        info.setId(id);
    }

    @Override
    public void setIdLong(AdCheZhiTouData info, long id) {
    
        info.setId((int)id);
    
    }




      
      
          
          
      

    

    
    /**
     * Deletes from the database for table AdCheZhiTouData with connection
     */
    @Override
    public boolean deleteByKey(  Integer Id,   Connection cn )
    {
        return 1 <= dbHelper.executeNoQuery(getDeleteSql(), new Object[]{  Id   }, cn);
    }

    /**
    * Deletes from the database for table AdCheZhiTouData
    */
    @Override
    public boolean deleteByKey(  Integer Id )
    {
        return 1 <= dbHelper.executeNoQuery(getDeleteSql(), new Object[]{  Id   });
    }








    /**
     * Inserts the dummy record of AdCheZhiTouData object values into the database.
     */
    @Override
    public boolean insertDummy()
    {
        AdCheZhiTouData  info = new AdCheZhiTouData();
        java.util.Random rnd = new java.util.Random();
    
        info.setReason(Integer.toString(Math.abs(rnd.nextInt(Integer.MAX_VALUE)), 36));
    
        info.setId(getNextKey());
    
        return this.insert(info);
    }

}
