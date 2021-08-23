package com.bixuebihui.test.dal;

/*
 *  HistoryHandlebarsTemplateList
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
public class HistoryHandlebarsTemplateList  extends BaseList<HistoryHandlebarsTemplate, Integer>
{
    
    /**
      * Don't direct use the HistoryHandlebarsTemplateList, use HistoryHandlebarsTemplateManager instead.
      */
    protected HistoryHandlebarsTemplateList(DataSource ds)
    {
        super(ds);
    }

    
    @SuppressWarnings("AlibabaClassNamingShouldBeCamel")
    public static final class F{
        public static final String HISTORY_ID = "history_id";
        public static final String TEMPLATE_ID = "template_id";
        public static final String TEMPLATE_CODE = "template_code";
        public static final String DESCRIPTION = "description";
        public static final String DATASOURCE_ID = "datasource_id";
        public static final String BASE_TEMPLATE_ID = "base_template_id";
        public static final String TEMPLATE_BODY = "template_body";
        public static final String OPERATOR_ID = "operator_id";
        public static final String VERSION = "version";
        public static final String IS_DEL = "is_del";
        public static final String CREATED_STIME = "created_stime";
        public static final String MODIFIED_STIME = "modified_stime";
        
        public static String[] getAllFields() {
            return new String[] { HISTORY_ID, TEMPLATE_ID, TEMPLATE_CODE, DESCRIPTION, DATASOURCE_ID, BASE_TEMPLATE_ID, TEMPLATE_BODY, OPERATOR_ID, VERSION, IS_DEL, CREATED_STIME, MODIFIED_STIME };
        }
    }

    
    protected String getDeleteSql(){
        return "delete from "+this.getTableName()+" where  history_id=? ";
    }

   @Override
   protected String getInsertSql(){
       return "insert into "+this.getTableName()+" ( template_id ,template_code ,description ,datasource_id ,base_template_id ,template_body ,operator_id ,version ,is_del ,created_stime ,modified_stime ) values (  ? ,  ? ,  ? ,  ? ,  ? ,  ? ,  ? ,  ? ,  ? ,  ? ,  ?  )";
   }


    @Override
    protected String getUpdateSql(){ 
        return "update "+this.getTableName()+" set   history_id=?,  template_id=?,  template_code=?,  description=?,  datasource_id=?,  base_template_id=?,  template_body=?,  operator_id=?,version= version +1,  is_del=?,  created_stime=?,  modified_stime=?  where history_id=?";
    }

    
   @Override
   protected Object[] getInsertObjs(HistoryHandlebarsTemplate info){
        return new Object[]{ info.getTemplateId(),info.getTemplateCode(),info.getDescription(),info.getDatasourceId(),info.getBaseTemplateId(),info.getTemplateBody(),info.getOperatorId(),info.getVersion(),info.getIsDel(),info.getCreatedStime(),info.getModifiedStime() };
   }

   @Override
   protected Object[] getUpdateObjs(HistoryHandlebarsTemplate  info){
      return new Object[]{ info.getTemplateId(),info.getTemplateCode(),info.getDescription(),info.getDatasourceId(),info.getBaseTemplateId(),info.getTemplateBody(),info.getOperatorId(),info.getIsDel(),info.getCreatedStime(),info.getModifiedStime(),info.getHistoryId() };
   }

    
    /**
     * Get table name.
     */
    @Override
    public String getTableName()
    {

      return  "history_handlebars_template";

    }

    
    /**
    * Get key name.
    */
    @Override
    public String getKeyName()
    {
        return  F.HISTORY_ID;
    }


      /**
        * Updates the object from a selected ResultSet.
        */
      @Override
      public HistoryHandlebarsTemplate mapRow (ResultSet r, int index) throws SQLException
      {
            HistoryHandlebarsTemplate res = new HistoryHandlebarsTemplate();
            res.setHistoryId(r.getInt(F.HISTORY_ID));
            res.setTemplateId(r.getInt(F.TEMPLATE_ID));
            res.setTemplateCode(r.getString(F.TEMPLATE_CODE));
            res.setDescription(r.getString(F.DESCRIPTION));
            res.setDatasourceId(r.getInt(F.DATASOURCE_ID));
            res.setBaseTemplateId(r.getInt(F.BASE_TEMPLATE_ID));
            res.setTemplateBody(r.getString(F.TEMPLATE_BODY));
            res.setOperatorId(r.getInt(F.OPERATOR_ID));
            res.setVersion(r.getInt(F.VERSION));
            res.setIsDel(r.getInt(F.IS_DEL));
            res.setCreatedStime(r.getTimestamp(F.CREATED_STIME));
            res.setModifiedStime(r.getTimestamp(F.MODIFIED_STIME));
            return res;
      }



    @Override
    public Integer getId(HistoryHandlebarsTemplate info) {
        return info.getHistoryId();
    }

    @Override
    public void setId(HistoryHandlebarsTemplate info, Integer id) {
    
        info.setHistoryId(id);
    }

    @Override
    public void setIdLong(HistoryHandlebarsTemplate info, long id) {
    
        info.setHistoryId((int)id);
    
    }


      
      
      

    
    

    /**
     * Updates the current object values into the database with version condition as an optimistic database lock.
     */
    public boolean updateByKeyAndVersion(HistoryHandlebarsTemplate  info, Connection cn)
    {
       return 1 == dbHelper.executeNoQuery(getUpdateSql()+" and version=?", new Object[]{
        
            info.getHistoryId(),
            
            info.getTemplateId(),
            
            info.getTemplateCode(),
            
            info.getDescription(),
            
            info.getDatasourceId(),
            
            info.getBaseTemplateId(),
            
            info.getTemplateBody(),
            
            info.getOperatorId(),
            
            
            info.getIsDel(),
            
            info.getCreatedStime(),
            
            info.getModifiedStime(),
            
        info.getHistoryId(),info.getVersion()
        }, cn );
    }

    public boolean updateByKeyAndVersion(HistoryHandlebarsTemplate  info)
    {
        return 1 == dbHelper.executeNoQuery(getUpdateSql()+" and version=?", new Object[]{
        
            info.getHistoryId(),
            
        
            info.getTemplateId(),
            
        
            info.getTemplateCode(),
            
        
            info.getDescription(),
            
        
            info.getDatasourceId(),
            
        
            info.getBaseTemplateId(),
            
        
            info.getTemplateBody(),
            
        
            info.getOperatorId(),
            
        
            
        
            info.getIsDel(),
            
        
            info.getCreatedStime(),
            
        
            info.getModifiedStime(),
            
        
        info.getHistoryId(),info.getVersion()
        });
    }
    

    
    /**
     * Deletes from the database for table history_handlebars_template with connection
     */
    @Override
    public boolean deleteByKey(  Integer history_id,   Connection cn )
    {
        return 1 <= dbHelper.executeNoQuery(getDeleteSql(), new Object[]{  history_id   }, cn);
    }

    /**
    * Deletes from the database for table history_handlebars_template
    */
    @Override
    public boolean deleteByKey(  Integer history_id )
    {
        return 1 <= dbHelper.executeNoQuery(getDeleteSql(), new Object[]{  history_id   });
    }








    /**
     * Inserts the dummy record of HistoryHandlebarsTemplate object values into the database.
     */
    @Override
    public boolean insertDummy()
    {
        HistoryHandlebarsTemplate  info = new HistoryHandlebarsTemplate();
        java.util.Random rnd = new java.util.Random();
    info.setTemplateCode(Integer.toString(Math.abs(rnd.nextInt(Integer.MAX_VALUE)), 36));
        info.setDescription(Integer.toString(Math.abs(rnd.nextInt(Integer.MAX_VALUE)), 36));
        info.setTemplateBody(Integer.toString(Math.abs(rnd.nextInt(Integer.MAX_VALUE)), 36));
        
        info.setHistoryId(getNextKey());
    
        return this.insert(info);
    }

}
