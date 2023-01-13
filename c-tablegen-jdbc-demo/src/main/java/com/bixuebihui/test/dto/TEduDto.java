package com.bixuebihui.test.dto;

/*
 *  TEduDto
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2022
 * email: xwx@live.cn
 */


import java.sql.*;
import java.io.Serializable;
import jakarta.validation.constraints.*;
import org.apache.commons.text.StringEscapeUtils;


import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.processing.Generated;

@Generated("com.github.yujiaao:tablegen")
@Schema(description ="教育程度")
public class TEduDto  implements Serializable {


    
    /**
    * id 这里是id! 
    */
    @Schema(title = "这里是id!")
    protected Integer  id;

    
    /**
    * degree 这里是名称！ 
    */
    @Size(max=100)
    @Schema(title = "这里是名称！")
    protected String  degree;




    public void setId(Integer id)
    {
      this.id = id;
    }
    public Integer getId()
    {
      return this.id;
    }

    public void setDegree(String degree)
    {
      this.degree = degree;
    }
    public String getDegree()
    {
      return this.degree;
    }


    
    public TEduDto()
    {
     
            id=0;
             
            degree="";
            
    }

    public String toXml()
    {
        StringBuilder s= new StringBuilder();
        String ln = System.getProperty("line.separator");
        s.append("<t_edu ");

        s.append(" id=\"").append(this.getId()).append("\"");
        
        s.append(" degree=\"").append(StringEscapeUtils.escapeXml11(this.getDegree())).append("\"");s.append(" />");
        s.append(ln);
        return s.toString();
    }
}
