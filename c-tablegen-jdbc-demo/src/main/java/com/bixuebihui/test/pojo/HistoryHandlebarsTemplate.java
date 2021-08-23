package com.bixuebihui.test.pojo;

/*
 *  HistoryHandlebarsTemplate
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2021
 * email: www@qsn.so
 */


import java.sql.*;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.apache.commons.text.StringEscapeUtils;


import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.processing.Generated;

@Generated("com.github.yujiaao:tablegen")
@Schema(description ="数据源模板历史记录表")
public class HistoryHandlebarsTemplate  implements Serializable {


    
    /**
    * history_id
    */
    
    protected Integer  historyId;

    
    /**
    * template_id
    */
    @NotNull
@Schema(title = "模板ID")
    protected Integer  templateId;

    
    /**
    * template_code
    */
    @Size(max=32)
@Schema(title = "模板code,需唯一")
    protected String  templateCode;

    
    /**
    * description
    */
    @Size(max=32)
@Schema(title = "模板说明")
    protected String  description;

    
    /**
    * datasource_id
    */
    @Schema(title = "json数据源id")
    protected Integer  datasourceId;

    
    /**
    * base_template_id
    */
    @Schema(title = "继承模板id")
    protected Integer  baseTemplateId;

    
    /**
    * template_body
    */
    @Size(max=65535)
@Schema(title = "模板主体")
    protected String  templateBody;

    
    /**
    * operator_id
    */
    @NotNull
@Schema(title = "操作用户id")
    protected Integer  operatorId;

    
    /**
    * version * NotNull, but has default value :0

    */
    @Schema(title = "版本号")
    protected Integer  version;

    
    /**
    * is_del * NotNull, but has default value :0

    */
    @Schema(title = "0：未删除；1：已删除")
    protected Integer  isDel;

    
    /**
    * created_stime * NotNull, but has default value :current_timestamp()

    */
    @Schema(title = "数据创建时间")
    protected Timestamp  createdStime;

    
    /**
    * modified_stime * NotNull, but has default value :current_timestamp()

    */
    @Schema(title = "数据更新时间")
    protected Timestamp  modifiedStime;




    public void setHistoryId(Integer historyId)
    {
      this.historyId = historyId;
    }
    public Integer getHistoryId()
    {
      return this.historyId;
    }

    public void setTemplateId(Integer templateId)
    {
      this.templateId = templateId;
    }
    public Integer getTemplateId()
    {
      return this.templateId;
    }

    public void setTemplateCode(String templateCode)
    {
      this.templateCode = templateCode;
    }
    public String getTemplateCode()
    {
      return this.templateCode;
    }

    public void setDescription(String description)
    {
      this.description = description;
    }
    public String getDescription()
    {
      return this.description;
    }

    public void setDatasourceId(Integer datasourceId)
    {
      this.datasourceId = datasourceId;
    }
    public Integer getDatasourceId()
    {
      return this.datasourceId;
    }

    public void setBaseTemplateId(Integer baseTemplateId)
    {
      this.baseTemplateId = baseTemplateId;
    }
    public Integer getBaseTemplateId()
    {
      return this.baseTemplateId;
    }

    public void setTemplateBody(String templateBody)
    {
      this.templateBody = templateBody;
    }
    public String getTemplateBody()
    {
      return this.templateBody;
    }

    public void setOperatorId(Integer operatorId)
    {
      this.operatorId = operatorId;
    }
    public Integer getOperatorId()
    {
      return this.operatorId;
    }

    public void setVersion(Integer version)
    {
      this.version = version;
    }
    public Integer getVersion()
    {
      return this.version;
    }

    public void setIsDel(Integer isDel)
    {
      this.isDel = isDel;
    }
    public Integer getIsDel()
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


    
    public HistoryHandlebarsTemplate()
    {
     
            historyId=0;
             
            templateId=0;
             
            templateCode="";
             
            description="";
             
            datasourceId=0;
             
            baseTemplateId=0;
             
            templateBody="";
             
            operatorId=0;
             
            version=0;
             
            isDel=0;
             
            createdStime=new Timestamp(System.currentTimeMillis());
             
            modifiedStime=new Timestamp(System.currentTimeMillis());
            
    }

    public String toXml()
    {
        StringBuilder s= new StringBuilder();
        String ln = System.getProperty("line.separator");
        s.append("<history_handlebars_template ");

        s.append(" historyId=\"").append(this.getHistoryId()).append("\"");
        
        s.append(" templateId=\"").append(this.getTemplateId()).append("\"");
        
        s.append(" templateCode=\"").append(StringEscapeUtils.escapeXml11(this.getTemplateCode())).append("\"");
        s.append(" description=\"").append(StringEscapeUtils.escapeXml11(this.getDescription())).append("\"");
        s.append(" datasourceId=\"").append(this.getDatasourceId()).append("\"");
        
        s.append(" baseTemplateId=\"").append(this.getBaseTemplateId()).append("\"");
        
        s.append(" templateBody=\"").append(StringEscapeUtils.escapeXml11(this.getTemplateBody())).append("\"");
        s.append(" operatorId=\"").append(this.getOperatorId()).append("\"");
        
        s.append(" version=\"").append(this.getVersion()).append("\"");
        
        s.append(" isDel=\"").append(this.getIsDel()).append("\"");
        
        s.append(" createdStime=\"").append(this.getCreatedStime()).append("\"");
        
        s.append(" modifiedStime=\"").append(this.getModifiedStime()).append("\"");
        s.append(" />");
        s.append(ln);
        return s.toString();
    }
}
