package com.bixuebihui.test.pojo;

/*
 *  InfoAuthorTermData
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2023
 * email: xwx@live.cn
 */


import java.sql.*;
import java.io.Serializable;
import jakarta.validation.constraints.*;
import org.apache.commons.text.StringEscapeUtils;
import javax.annotation.processing.Generated;



@Generated("com.github.yujiaao:tablegen")
public class InfoAuthorTermData  implements Serializable {


    
    /**
    * id 主键，自增 
    */
    protected Long  id;

    
    /**
    * is_del 是否删除；0-未删除，1-已删除，默认0 
    * NotNull, default value: 0
    */
    protected Byte  isDel;

    
    /**
    * created_stime 创建时间 
    * NotNull, default value: current_timestamp()
    */
    protected Timestamp  createdStime;

    
    /**
    * modified_stime 修改时间 
    * NotNull, default value: current_timestamp()
    */
    protected Timestamp  modifiedStime;

    
    /**
    * dt 日期，如：2023-02-28 
    */
    @NotNull
    protected Timestamp  dt;

    
    /**
    * date_type 周期类型 1：昨天，2：七天，3：30天 
    * NotNull, default value: 0
    */
    protected Integer  dateType;

    
    /**
    * author_id 之家用户ID（作者ID） 
    * NotNull, default value: 0
    */
    protected Integer  authorId;

    
    /**
    * info_type 内容分类--区分图文、视频，可以中文名，亦可以整型数字标识 
    * NotNull, default value: &#x27;0&#x27;
    */
    @Size(max=30)
    protected String  infoType;

    
    /**
    * publish_cnt 发布内容数 
    */
    @NotNull
    protected Long  publishCnt;

    
    /**
    * comment_cnt 评论数 
    */
    @NotNull
    protected Long  commentCnt;

    
    /**
    * praise_cnt 点赞数 
    */
    @NotNull
    protected Long  praiseCnt;

    
    /**
    * pv 图文浏览pvor视频播放vv 
    */
    @NotNull
    protected Long  pv;




    public void setId(Long id)
    {
      this.id = id;
    }
    public Long getId()
    {
      return this.id;
    }

    public void setIsDel(Byte isDel)
    {
      this.isDel = isDel;
    }
    public Byte getIsDel()
    {
      return this.isDel;
    }

    public void setCreatedStime(Timestamp createdStime)
    {
      this.createdStime = createdStime;
    }
    public Timestamp getCreatedStime()
    {
      return this.createdStime;
    }

    public void setModifiedStime(Timestamp modifiedStime)
    {
      this.modifiedStime = modifiedStime;
    }
    public Timestamp getModifiedStime()
    {
      return this.modifiedStime;
    }

    public void setDt(Timestamp dt)
    {
      this.dt = dt;
    }
    public Timestamp getDt()
    {
      return this.dt;
    }

    public void setDateType(Integer dateType)
    {
      this.dateType = dateType;
    }
    public Integer getDateType()
    {
      return this.dateType;
    }

    public void setAuthorId(Integer authorId)
    {
      this.authorId = authorId;
    }
    public Integer getAuthorId()
    {
      return this.authorId;
    }

    public void setInfoType(String infoType)
    {
      this.infoType = infoType;
    }
    public String getInfoType()
    {
      return this.infoType;
    }

    public void setPublishCnt(Long publishCnt)
    {
      this.publishCnt = publishCnt;
    }
    public Long getPublishCnt()
    {
      return this.publishCnt;
    }

    public void setCommentCnt(Long commentCnt)
    {
      this.commentCnt = commentCnt;
    }
    public Long getCommentCnt()
    {
      return this.commentCnt;
    }

    public void setPraiseCnt(Long praiseCnt)
    {
      this.praiseCnt = praiseCnt;
    }
    public Long getPraiseCnt()
    {
      return this.praiseCnt;
    }

    public void setPv(Long pv)
    {
      this.pv = pv;
    }
    public Long getPv()
    {
      return this.pv;
    }


    
    public InfoAuthorTermData()
    {
     
            id=0L;
             
            isDel=(byte)0;
             
            createdStime=new Timestamp(System.currentTimeMillis());
             
            modifiedStime=new Timestamp(System.currentTimeMillis());
             
            dt=new Timestamp(System.currentTimeMillis());
             
            dateType=0;
             
            authorId=0;
             
            infoType="";
             
            publishCnt=0L;
             
            commentCnt=0L;
             
            praiseCnt=0L;
             
            pv=0L;
            
    }

    public String toXml()
    {
        StringBuilder s= new StringBuilder();
        String ln = System.getProperty("line.separator");
        s.append("<info_author_term_data ");

        s.append(" id=\"").append(this.getId()).append("\"");
        
        s.append(" isDel=\"").append(this.getIsDel()).append("\"");
        
        s.append(" createdStime=\"").append(this.getCreatedStime()).append("\"");
        
        s.append(" modifiedStime=\"").append(this.getModifiedStime()).append("\"");
        
        s.append(" dt=\"").append(this.getDt()).append("\"");
        
        s.append(" dateType=\"").append(this.getDateType()).append("\"");
        
        s.append(" authorId=\"").append(this.getAuthorId()).append("\"");
        
        s.append(" infoType=\"").append(StringEscapeUtils.escapeXml11(this.getInfoType())).append("\"");
        s.append(" publishCnt=\"").append(this.getPublishCnt()).append("\"");
        
        s.append(" commentCnt=\"").append(this.getCommentCnt()).append("\"");
        
        s.append(" praiseCnt=\"").append(this.getPraiseCnt()).append("\"");
        
        s.append(" pv=\"").append(this.getPv()).append("\"");
        s.append(" />");
        s.append(ln);
        return s.toString();
    }
}
