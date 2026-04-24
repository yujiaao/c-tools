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
import java.time.*;
import java.io.Serializable;
import jakarta.validation.constraints.*;
import org.apache.commons.text.StringEscapeUtils;

import javax.annotation.processing.Generated;

@Generated("com.github.yujiaao:tablegen")
public class HistoryHandlebarsTemplate  implements Serializable {


    
    /**
    * history_id
    */
    
    protected Integer  historyId;

    
    /**
    * template_id
    */
    @NotNull
    protected Integer  templateId;

    
    /**
    * template_code
    */
    @Size(max=32)
    protected String  templateCode;

    
    /**
    * description
    */
    @Size(max=32)
    protected String  description;

    
    /**
    * datasource_id
    */
    protected Integer  datasourceId;

    
    /**
    * base_template_id
    */
    protected Integer  baseTemplateId;

    
    /**
    * template_body
    */
    @Size(max=65535)
    protected String  templateBody;

    
    /**
    * operator_id
    */
    @NotNull
    protected Integer  operatorId;

    
    /**
    * version * NotNull, but has default value :0

    */
    protected Integer  version;

    
    /**
    * is_del * NotNull, but has default value :0

    */
    protected Integer  isDel;

    
    /**
    * created_stime * NotNull, but has default value :current_timestamp()

    */
    protected LocalDateTime  createdStime;

    
    /**
    * modified_stime * NotNull, but has default value :current_timestamp()

    */
    protected LocalDateTime  modifiedStime;




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

    public void setCreatedStime(LocalDateTime createdStime)
    {
      this.createdStime = createdStime;
    }
    public LocalDateTime getCreatedStime()
    {
      return this.createdStime;
    }

    public void setModifiedStime(LocalDateTime modifiedStime)
    {
      this.modifiedStime = modifiedStime;
    }
    public LocalDateTime getModifiedStime()
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
             
            createdStime=LocalDateTime.now();
             
            modifiedStime=LocalDateTime.now();
            
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
