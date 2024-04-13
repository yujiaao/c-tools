package com.bixuebihui.test.dal;

/*
 *  InfoAuthorTermDataList
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
import java.util.Set;
import javax.sql.DataSource;
import com.bixuebihui.test.business.*;
import com.bixuebihui.test.pojo.*;
import com.bixuebihui.jdbc.RowMapperResultReader;
import com.bixuebihui.DbException;

import com.bixuebihui.test.BaseList;

import javax.annotation.processing.Generated;

@Generated("com.github.yujiaao:tablegen")
public class InfoAuthorTermDataList  extends BaseList<InfoAuthorTermData, List<Object>>
{
    
    /**
      * Don't direct use the InfoAuthorTermDataList, use InfoAuthorTermDataManager instead.
      */

    protected InfoAuthorTermDataList(DataSource ds)
    {
        super(ds);
    }


    
    @SuppressWarnings("AlibabaClassNamingShouldBeCamel")
    public static final class F{
        public static final String ID = "id";
        public static final String IS_DEL = "is_del";
        public static final String CREATED_STIME = "created_stime";
        public static final String MODIFIED_STIME = "modified_stime";
        public static final String DT = "dt";
        public static final String DATE_TYPE = "date_type";
        public static final String AUTHOR_ID = "author_id";
        public static final String INFO_TYPE = "info_type";
        public static final String PUBLISH_CNT = "publish_cnt";
        public static final String COMMENT_CNT = "comment_cnt";
        public static final String PRAISE_CNT = "praise_cnt";
        public static final String PV = "pv";
        
        public static String[] getAllFields() {
            return new String[] { ID, IS_DEL, CREATED_STIME, MODIFIED_STIME, DT, DATE_TYPE, AUTHOR_ID, INFO_TYPE, PUBLISH_CNT, COMMENT_CNT, PRAISE_CNT, PV };
        }
    }

    @Override
    public List<String> getAllFields(){
        return Arrays.asList(F.getAllFields());
    }


    
    protected String getDeleteSql(){
        return "delete from "+this.getTableName()+" where  id=? and  dt=? ";
    }

   @Override
   protected String getInsertSql(){
       return "insert into info_author_term_data"
    +" ( is_del, created_stime, modified_stime, dt, date_type, author_id, info_type, publish_cnt, comment_cnt, praise_cnt, pv)"
    +" values ("
    +"?,?,?,?,?,?,?,?,?,?,?)";
   }


    @Override
    protected String getUpdateSql(){
        return "update info_author_term_data set "
    +"is_del=?,created_stime=?,modified_stime=?,date_type=?,author_id=?,info_type=?,publish_cnt=?,comment_cnt=?,praise_cnt=?,pv=? where id=? and dt=?";
    }

    
   @Override
   protected Object[] getInsertObjs(InfoAuthorTermData info){
        return new Object[]{ info.getIsDel(),info.getCreatedStime(),info.getModifiedStime(),info.getDt(),info.getDateType(),info.getAuthorId(),info.getInfoType(),info.getPublishCnt(),info.getCommentCnt(),info.getPraiseCnt(),info.getPv() };
   }

   @Override
   protected Object[] getUpdateObjs(InfoAuthorTermData  info){
      return new Object[]{ info.getIsDel(),info.getCreatedStime(),info.getModifiedStime(),info.getDateType(),info.getAuthorId(),info.getInfoType(),info.getPublishCnt(),info.getCommentCnt(),info.getPraiseCnt(),info.getPv(),info.getId() , info.getDt() };
   }



    /**
     * Get table name.
     */
    @Override
    public String getTableName()
    {

      return  "info_author_term_data";

    }

    
    /**
    * Get key name.
    */
    @Override
    public String getKeyName()
    { 
        return  F.ID + "," +  F.DT ;
    }


      /**
        * Updates the object from a selected ResultSet.
        */
      @Override
      public InfoAuthorTermData mapRow (ResultSet r, int index, Set<String> fields) throws SQLException
      {
            InfoAuthorTermData res = new InfoAuthorTermData();
            if (containsIgnoreCase(F.ID, fields)){
                  res.setId(r.getLong(F.ID));
            }
            if (containsIgnoreCase(F.IS_DEL, fields)){
                  res.setIsDel(r.getByte(F.IS_DEL));
            }
            if (containsIgnoreCase(F.CREATED_STIME, fields)){
                  res.setCreatedStime(r.getTimestamp(F.CREATED_STIME));
            }
            if (containsIgnoreCase(F.MODIFIED_STIME, fields)){
                  res.setModifiedStime(r.getTimestamp(F.MODIFIED_STIME));
            }
            if (containsIgnoreCase(F.DT, fields)){
                  res.setDt(r.getTimestamp(F.DT));
            }
            if (containsIgnoreCase(F.DATE_TYPE, fields)){
                  res.setDateType(r.getInt(F.DATE_TYPE));
            }
            if (containsIgnoreCase(F.AUTHOR_ID, fields)){
                  res.setAuthorId(r.getInt(F.AUTHOR_ID));
            }
            if (containsIgnoreCase(F.INFO_TYPE, fields)){
                  res.setInfoType(r.getString(F.INFO_TYPE));
            }
            if (containsIgnoreCase(F.PUBLISH_CNT, fields)){
                  res.setPublishCnt(r.getLong(F.PUBLISH_CNT));
            }
            if (containsIgnoreCase(F.COMMENT_CNT, fields)){
                  res.setCommentCnt(r.getLong(F.COMMENT_CNT));
            }
            if (containsIgnoreCase(F.PRAISE_CNT, fields)){
                  res.setPraiseCnt(r.getLong(F.PRAISE_CNT));
            }
            if (containsIgnoreCase(F.PV, fields)){
                  res.setPv(r.getLong(F.PV));
            }
            return res;
      }



    @Override
    public List<Object> getId(InfoAuthorTermData info) {
    
        return List.of(info.getId(), info.getDt());
        
    }

    @Override
    public void setId(InfoAuthorTermData info, List<Object> id) {
    
        info.setId((Long) id.get(0) );
        info.setDt((Timestamp) id.get(1) );
        
        
    }

    @Override
    public void setIdLong(InfoAuthorTermData info, long id) {

    throw new UnsupportedOperationException("This method is not supported, because table info_author_term_data has more than one key!");
    

    }




      
          public List<Object> getNextKey(){
            throw new IllegalStateException("no autokey for union keys");
          }
      
      

    
        public boolean logicDeleteByIds(List<List<Object>>  keys)
        {
            return keys.stream().map(item ->
                this.ar()
                    .eq(F.ID, item.get(0))
                    .eq(F.DT, item.get(1))
                    .update(F.IS_DEL, 1)
                ).reduce((a, b) -> a + b).orElse(0) == keys.size();
        }

    

    

    
    /**
     * Deletes from the database for table info_author_term_data with connection
     */
    
    public boolean deleteByKey(  Long id,  Timestamp dt,   Connection cn )
    {
        return 1 <= dbHelper.executeNoQuery(getDeleteSql(), new Object[]{  id ,   dt   }, cn);
    }

    /**
    * Deletes from the database for table info_author_term_data
    */
    
    public boolean deleteByKey(  Long id ,  Timestamp dt )
    {
        return 1 <= dbHelper.executeNoQuery(getDeleteSql(), new Object[]{  id ,   dt   });
    }








    /**
     * Inserts the dummy record of InfoAuthorTermData object values into the database.
     */
    @Override
    public boolean insertDummy()
    {
        InfoAuthorTermData  info = new InfoAuthorTermData();
        java.util.Random rnd = new java.util.Random();
    
        return this.insert(info);
    }

}
