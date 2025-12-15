package com.bixuebihui.test.dto;

/*
 *  InfoAuthorTermDataDto
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2023
 * email: xwx@live.cn
 */


import java.sql.*;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.apache.commons.text.StringEscapeUtils;

import javax.annotation.processing.Generated;




import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiModelProperty;
import com.sdadas.spring2ts.annotations.SharedModel;

@Api(tags="创作者作品消费数据统计明细-周期表")
@SharedModel("")

public class InfoAuthorTermDataDto  implements Serializable {


    
    /**
     * id 主键，自增 
     */
    @ApiModelProperty("主键，自增")
    protected Long  id;

    
    /**
     * is_del 是否删除；0-未删除，1-已删除，默认0 
    * NotNull, default value: 0
     */
    @ApiModelProperty("是否删除；0-未删除，1-已删除，默认0")
    protected Byte  isDel;

    
    /**
     * created_stime 创建时间 
    * NotNull, default value: current_timestamp()
     */
    @ApiModelProperty("创建时间")
    protected Timestamp  createdStime;

    
    /**
     * modified_stime 修改时间 
    * NotNull, default value: current_timestamp()
     */
    @ApiModelProperty("修改时间")
    protected Timestamp  modifiedStime;

    
    /**
     * dt 日期，如：2023-02-28 
     */
    @ApiModelProperty("日期，如：2023-02-28")
    protected Timestamp  dt;

    
    /**
     * date_type 周期类型 1：昨天，2：七天，3：30天 
    * NotNull, default value: 0
     */
    @ApiModelProperty("周期类型 1：昨天，2：七天，3：30天")
    protected Integer  dateType;

    
    /**
     * author_id 之家用户ID（作者ID） 
    * NotNull, default value: 0
     */
    @ApiModelProperty("之家用户ID（作者ID）")
    protected Integer  authorId;

    
    /**
     * info_type 内容分类--区分图文、视频，可以中文名，亦可以整型数字标识 
    * NotNull, default value: &#x27;0&#x27;
     */
    @ApiModelProperty("内容分类--区分图文、视频，可以中文名，亦可以整型数字标识")
    protected String  infoType;

    
    /**
     * publish_cnt 发布内容数 
     */
    @ApiModelProperty("发布内容数")
    protected Long  publishCnt;

    
    /**
     * comment_cnt 评论数 
     */
    @ApiModelProperty("评论数")
    protected Long  commentCnt;

    
    /**
     * praise_cnt 点赞数 
     */
    @ApiModelProperty("点赞数")
    protected Long  praiseCnt;

    
    /**
     * pv 图文浏览pvor视频播放vv 
     */
    @ApiModelProperty("图文浏览pvor视频播放vv")
    protected Long  pv;




    

    /**
     * id 主键，自增 
     */
    public void setId(Long id)
    {
      this.id = id;
    }
    /**
     * id 主键，自增 
     */
    public Long getId()
    {
      return this.id;
    }
    

    

    /**
     * is_del 是否删除；0-未删除，1-已删除，默认0 
    * NotNull, default value: 0
     */
    public void setIsDel(Byte isDel)
    {
      this.isDel = isDel;
    }
    /**
     * is_del 是否删除；0-未删除，1-已删除，默认0 
    * NotNull, default value: 0
     */
    public Byte getIsDel()
    {
      return this.isDel;
    }
    

    

    /**
     * created_stime 创建时间 
    * NotNull, default value: current_timestamp()
     */
    public void setCreatedStime(Timestamp createdStime)
    {
      this.createdStime = createdStime;
    }
    /**
     * created_stime 创建时间 
    * NotNull, default value: current_timestamp()
     */
    public Timestamp getCreatedStime()
    {
      return this.createdStime;
    }
    

    

    /**
     * modified_stime 修改时间 
    * NotNull, default value: current_timestamp()
     */
    public void setModifiedStime(Timestamp modifiedStime)
    {
      this.modifiedStime = modifiedStime;
    }
    /**
     * modified_stime 修改时间 
    * NotNull, default value: current_timestamp()
     */
    public Timestamp getModifiedStime()
    {
      return this.modifiedStime;
    }
    

    

    /**
     * dt 日期，如：2023-02-28 
     */
    public void setDt(Timestamp dt)
    {
      this.dt = dt;
    }
    /**
     * dt 日期，如：2023-02-28 
     */
    public Timestamp getDt()
    {
      return this.dt;
    }
    

    

    /**
     * date_type 周期类型 1：昨天，2：七天，3：30天 
    * NotNull, default value: 0
     */
    public void setDateType(Integer dateType)
    {
      this.dateType = dateType;
    }
    /**
     * date_type 周期类型 1：昨天，2：七天，3：30天 
    * NotNull, default value: 0
     */
    public Integer getDateType()
    {
      return this.dateType;
    }
    

    

    /**
     * author_id 之家用户ID（作者ID） 
    * NotNull, default value: 0
     */
    public void setAuthorId(Integer authorId)
    {
      this.authorId = authorId;
    }
    /**
     * author_id 之家用户ID（作者ID） 
    * NotNull, default value: 0
     */
    public Integer getAuthorId()
    {
      return this.authorId;
    }
    

    

    /**
     * info_type 内容分类--区分图文、视频，可以中文名，亦可以整型数字标识 
    * NotNull, default value: &#x27;0&#x27;
     */
    public void setInfoType(String infoType)
    {
      this.infoType = infoType;
    }
    /**
     * info_type 内容分类--区分图文、视频，可以中文名，亦可以整型数字标识 
    * NotNull, default value: &#x27;0&#x27;
     */
    public String getInfoType()
    {
      return this.infoType;
    }
    

    

    /**
     * publish_cnt 发布内容数 
     */
    public void setPublishCnt(Long publishCnt)
    {
      this.publishCnt = publishCnt;
    }
    /**
     * publish_cnt 发布内容数 
     */
    public Long getPublishCnt()
    {
      return this.publishCnt;
    }
    

    

    /**
     * comment_cnt 评论数 
     */
    public void setCommentCnt(Long commentCnt)
    {
      this.commentCnt = commentCnt;
    }
    /**
     * comment_cnt 评论数 
     */
    public Long getCommentCnt()
    {
      return this.commentCnt;
    }
    

    

    /**
     * praise_cnt 点赞数 
     */
    public void setPraiseCnt(Long praiseCnt)
    {
      this.praiseCnt = praiseCnt;
    }
    /**
     * praise_cnt 点赞数 
     */
    public Long getPraiseCnt()
    {
      return this.praiseCnt;
    }
    

    

    /**
     * pv 图文浏览pvor视频播放vv 
     */
    public void setPv(Long pv)
    {
      this.pv = pv;
    }
    /**
     * pv 图文浏览pvor视频播放vv 
     */
    public Long getPv()
    {
      return this.pv;
    }
    


    
    public InfoAuthorTermDataDto()
    {
 
        id=0L;
         
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
