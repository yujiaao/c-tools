package com.autohome.chejiahao.imagetext.dao.view.dal;

/*
 *  NeteasyVideoAuditingViewList
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
import com.autohome.chejiahao.imagetext.dao.view.business.*;
import com.autohome.chejiahao.imagetext.dao.view.pojo.*;
import com.bixuebihui.jdbc.RowMapperResultReader;
import com.bixuebihui.DbException;

import com.bixuebihui.jdbc.AbstractViewBaseDao;

import javax.annotation.processing.Generated;

@Generated("com.github.yujiaao:tablegen")
public class NeteasyVideoAuditingViewList  extends AbstractViewBaseDao<NeteasyVideoAuditingView, Integer> 
{
    
    /**
      * Don't direct use the NeteasyVideoAuditingViewList, use NeteasyVideoAuditingViewManager instead.
      */

    protected NeteasyVideoAuditingViewList(DataSource readWrite, DataSource readOnly)
    {
        super(readWrite,readOnly);
    }


    
    @SuppressWarnings("AlibabaClassNamingShouldBeCamel")
    public static final class F{
        public static final String MONITORTYPE = "monitortype";
        public static final String CAUSECODE = "causecode";
        public static final String INFOID = "infoid";
        public static final String INFOTYPE = "infotype";
        public static final String TITLE = "title";
        public static final String NICKNAME = "nickname";
        public static final String PUBLISHTIME = "publishtime";
        public static final String STATUS = "status";
        public static final String RECYCLED = "recycled";
        public static final String ISWAITUI = "iswaitui";
        
        public static String[] getAllFields() {
            return new String[] { MONITORTYPE, CAUSECODE, INFOID, INFOTYPE, TITLE, NICKNAME, PUBLISHTIME, STATUS, RECYCLED, ISWAITUI };
        }
    }



    /**
    * Get table view sql.
    */
    @Override
    protected String getCoreSql()
    {
    
    
        return " select MonitorType,T.CauseCode,T.InfoId,I.InfoType,I.Title,U.NickName,I.PublishTime,I.Status,I.Recycled,I.IsWaiTui from InfoYellowMonitor T with(nolock)  inner join Infos I with(nolock) on T.InfoId=i.InfoId inner join Users U with(nolock) on I.AuthorId=U.UserId where T.MonitorType=2 and T.status=1 and Label in (1,1001,2001) order by T.created_stime,T.infoid desc";
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

      return "("+this.getCoreSql()+") as neteasy_video_auditing_view";
    }

    
    /**
    * Get key name.
    */
    @Override
    public String getKeyName()
    {
        return  F.MONITORTYPE;
    }


      /**
        * Updates the object from a selected ResultSet.
        */
      @Override
      public NeteasyVideoAuditingView mapRow (ResultSet r, int index, Set<String> fields) throws SQLException
      {
            NeteasyVideoAuditingView res = new NeteasyVideoAuditingView();
            if (containsIgnoreCase(F.MONITORTYPE, fields)){
                  res.setMonitortype(r.getInt(F.MONITORTYPE));
            }
            if (containsIgnoreCase(F.CAUSECODE, fields)){
                  res.setCausecode(r.getString(F.CAUSECODE));
            }
            if (containsIgnoreCase(F.INFOID, fields)){
                  res.setInfoid(r.getLong(F.INFOID));
            }
            if (containsIgnoreCase(F.INFOTYPE, fields)){
                  res.setInfotype(r.getInt(F.INFOTYPE));
            }
            if (containsIgnoreCase(F.TITLE, fields)){
                  res.setTitle(r.getString(F.TITLE));
            }
            if (containsIgnoreCase(F.NICKNAME, fields)){
                  res.setNickname(r.getString(F.NICKNAME));
            }
            if (containsIgnoreCase(F.PUBLISHTIME, fields)){
                  res.setPublishtime(r.getTimestamp(F.PUBLISHTIME));
            }
            if (containsIgnoreCase(F.STATUS, fields)){
                  res.setStatus(r.getInt(F.STATUS));
            }
            if (containsIgnoreCase(F.RECYCLED, fields)){
                  res.setRecycled(r.getBoolean(F.RECYCLED));
            }
            if (containsIgnoreCase(F.ISWAITUI, fields)){
                  res.setIswaitui(r.getBoolean(F.ISWAITUI));
            }
            return res;
      }



    @Override
    public Integer getId(NeteasyVideoAuditingView info) {
        return info.getMonitortype();
    }











}
