package com.bixuebihui.test.cmd;

import java.sql.*;



import io.swagger.annotations.ApiModelProperty;

/*
 *  InfoAuthorTermDataSearchCmd
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2023
 * email: xwx@live.cn
 */


/**
* @author xwx
* https://stackoverflow.com/questions/26612404/spring-map-get-request-parameters-to-pojo-automatically/26612778#26612778
*/
public class InfoAuthorTermDataSearchCmd {



    
        /**
        * id 主键，自增 
        *  change type of Long to List<Long> will generate 'in' sql condition.
        */
        @ApiModelProperty("主键，自增")
        protected Long  id;
    
    
        /**
        * is_del 是否删除；0-未删除，1-已删除，默认0 
    * NotNull, default value: 0
        *  change type of Byte to List<Byte> will generate 'in' sql condition.
        */
        @ApiModelProperty("是否删除；0-未删除，1-已删除，默认0")
        protected Byte  isDel;
    
    /**
        * left range of date/time type: created_stime 创建时间 
    * NotNull, default value: current_timestamp()
        */
        protected Timestamp  begin_createdStime;

        /**
        * right range of date/time type:  created_stime 创建时间 
    * NotNull, default value: current_timestamp()
        */
        protected Timestamp  end_createdStime;

    
    /**
        * left range of date/time type: modified_stime 修改时间 
    * NotNull, default value: current_timestamp()
        */
        protected Timestamp  begin_modifiedStime;

        /**
        * right range of date/time type:  modified_stime 修改时间 
    * NotNull, default value: current_timestamp()
        */
        protected Timestamp  end_modifiedStime;

    
    /**
        * left range of date/time type: dt 日期，如：2023-02-28 
        */
        protected Timestamp  begin_dt;

        /**
        * right range of date/time type:  dt 日期，如：2023-02-28 
        */
        protected Timestamp  end_dt;

    
    
        /**
        * date_type 周期类型 1：昨天，2：七天，3：30天 
    * NotNull, default value: 0
        *  change type of Integer to List<Integer> will generate 'in' sql condition.
        */
        @ApiModelProperty("周期类型 1：昨天，2：七天，3：30天")
        protected Integer  dateType;
    
    
        /**
        * author_id 之家用户ID（作者ID） 
    * NotNull, default value: 0
        *  change type of Integer to List<Integer> will generate 'in' sql condition.
        */
        @ApiModelProperty("之家用户ID（作者ID）")
        protected Integer  authorId;
    
    
        /**
        * info_type 内容分类--区分图文、视频，可以中文名，亦可以整型数字标识 
    * NotNull, default value: &#x27;0&#x27;
        *  change type of String to List<String> will generate 'in' sql condition.
        */
        @ApiModelProperty("内容分类--区分图文、视频，可以中文名，亦可以整型数字标识")
        protected String  infoType;
    
    
        /**
        * publish_cnt 发布内容数 
        *  change type of Long to List<Long> will generate 'in' sql condition.
        */
        @ApiModelProperty("发布内容数")
        protected Long  publishCnt;
    
    
        /**
        * comment_cnt 评论数 
        *  change type of Long to List<Long> will generate 'in' sql condition.
        */
        @ApiModelProperty("评论数")
        protected Long  commentCnt;
    
    
        /**
        * praise_cnt 点赞数 
        *  change type of Long to List<Long> will generate 'in' sql condition.
        */
        @ApiModelProperty("点赞数")
        protected Long  praiseCnt;
    
    
        /**
        * pv 图文浏览pvor视频播放vv 
        *  change type of Long to List<Long> will generate 'in' sql condition.
        */
        @ApiModelProperty("图文浏览pvor视频播放vv")
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



    public void setBegin_createdStime(Timestamp createdStime)
    {
        this.begin_createdStime = createdStime;
    }
    public Timestamp getBegin_createdStime()
    {
        return this.begin_createdStime;
    }
    public void setEnd_createdStime(Timestamp createdStime)
    {
        this.end_createdStime = createdStime;
    }
    public Timestamp getEnd_createdStime()
    {
        return this.end_createdStime;
    }




    public void setBegin_modifiedStime(Timestamp modifiedStime)
    {
        this.begin_modifiedStime = modifiedStime;
    }
    public Timestamp getBegin_modifiedStime()
    {
        return this.begin_modifiedStime;
    }
    public void setEnd_modifiedStime(Timestamp modifiedStime)
    {
        this.end_modifiedStime = modifiedStime;
    }
    public Timestamp getEnd_modifiedStime()
    {
        return this.end_modifiedStime;
    }




    public void setBegin_dt(Timestamp dt)
    {
        this.begin_dt = dt;
    }
    public Timestamp getBegin_dt()
    {
        return this.begin_dt;
    }
    public void setEnd_dt(Timestamp dt)
    {
        this.end_dt = dt;
    }
    public Timestamp getEnd_dt()
    {
        return this.end_dt;
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




}
