package com.autohome.chejiahao.imagetext.dao.view.pojo;

/*
 *  NeteasyVideoAuditingView
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2022
 * email: xwx@live.cn
 */


import java.sql.*;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.apache.commons.text.StringEscapeUtils;


import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.processing.Generated;

@Generated("com.github.yujiaao:tablegen")
@Schema(description =" select MonitorType,T.CauseCode,T.InfoId,I.InfoType,I.Title,U.NickName,I.PublishTime,I.Status,I.Recycled,I.IsWaiTui from InfoYellowMonitor T with(nolock)  inner join Infos I with(nolock) on T.InfoId&#x3D;i.InfoId inner join Users U with(nolock) on I.AuthorId&#x3D;U.UserId where T.MonitorType&#x3D;2 and T.status&#x3D;1 and Label in (1,1001,2001) order by T.created_stime,T.infoid desc")
public class NeteasyVideoAuditingView  implements Serializable {


    
    /**
    * monitortype monitortype 
    */
    @NotNull
    protected Integer  monitortype;

    
    /**
    * causecode causecode 
    */
    @Size(max=100)
    @NotNull
    protected String  causecode;

    
    /**
    * infoid infoid 
    */
    @NotNull
    protected Long  infoid;

    
    /**
    * infotype infotype 
    */
    @NotNull
    protected Integer  infotype;

    
    /**
    * title title 
    */
    @Size(max=200)
    @NotNull
    protected String  title;

    
    /**
    * nickname nickname 
    */
    @Size(max=50)
    @NotNull
    protected String  nickname;

    
    /**
    * publishtime publishtime 
    */
    
    protected Timestamp  publishtime;

    
    /**
    * status status 
    */
    @NotNull
    protected Integer  status;

    
    /**
    * recycled recycled 
    */
    @NotNull
    protected Boolean  recycled;

    
    /**
    * iswaitui iswaitui 
    */
    
    protected Boolean  iswaitui;




    public void setMonitortype(Integer monitortype)
    {
      this.monitortype = monitortype;
    }
    public Integer getMonitortype()
    {
      return this.monitortype;
    }

    public void setCausecode(String causecode)
    {
      this.causecode = causecode;
    }
    public String getCausecode()
    {
      return this.causecode;
    }

    public void setInfoid(Long infoid)
    {
      this.infoid = infoid;
    }
    public Long getInfoid()
    {
      return this.infoid;
    }

    public void setInfotype(Integer infotype)
    {
      this.infotype = infotype;
    }
    public Integer getInfotype()
    {
      return this.infotype;
    }

    public void setTitle(String title)
    {
      this.title = title;
    }
    public String getTitle()
    {
      return this.title;
    }

    public void setNickname(String nickname)
    {
      this.nickname = nickname;
    }
    public String getNickname()
    {
      return this.nickname;
    }

    public void setPublishtime(Timestamp publishtime)
    {
      this.publishtime = publishtime;
    }
    public Timestamp getPublishtime()
    {
      return this.publishtime;
    }

    public void setStatus(Integer status)
    {
      this.status = status;
    }
    public Integer getStatus()
    {
      return this.status;
    }

    public void setRecycled(Boolean recycled)
    {
      this.recycled = recycled;
    }
    public Boolean getRecycled()
    {
      return this.recycled;
    }

    public void setIswaitui(Boolean iswaitui)
    {
      this.iswaitui = iswaitui;
    }
    public Boolean getIswaitui()
    {
      return this.iswaitui;
    }


    
    public NeteasyVideoAuditingView()
    {
     
            monitortype=0;
             
            causecode="";
             
            infoid=0L;
             
            infotype=0;
             
            title="";
             
            nickname="";
             
            publishtime=new Timestamp(System.currentTimeMillis());
             
            status=0;
             
            recycled=Boolean.FALSE;
             
            iswaitui=Boolean.FALSE;
            
    }

    public String toXml()
    {
        StringBuilder s= new StringBuilder();
        String ln = System.getProperty("line.separator");
        s.append("<neteasy_video_auditing_view ");

        s.append(" monitortype=\"").append(this.getMonitortype()).append("\"");
        
        s.append(" causecode=\"").append(StringEscapeUtils.escapeXml11(this.getCausecode())).append("\"");
        s.append(" infoid=\"").append(this.getInfoid()).append("\"");
        
        s.append(" infotype=\"").append(this.getInfotype()).append("\"");
        
        s.append(" title=\"").append(StringEscapeUtils.escapeXml11(this.getTitle())).append("\"");
        s.append(" nickname=\"").append(StringEscapeUtils.escapeXml11(this.getNickname())).append("\"");
        s.append(" publishtime=\"").append(this.getPublishtime()).append("\"");
        
        s.append(" status=\"").append(this.getStatus()).append("\"");
        
        s.append(" recycled=\"").append(this.getRecycled()).append("\"");
        
        s.append(" iswaitui=\"").append(this.getIswaitui()).append("\"");
        s.append(" />");
        s.append(ln);
        return s.toString();
    }
}
