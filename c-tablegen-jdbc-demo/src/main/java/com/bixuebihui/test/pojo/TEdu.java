package com.bixuebihui.test.pojo;

/*
 *  TEdu
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



import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

@Api(tags="教育程度")


@Generated("com.github.yujiaao:tablegen")
public class TEdu  implements Serializable {


    
    /**
    * id 这里是id! 
    */
    @ApiModelProperty("这里是id!")
    protected Integer  id;

    
    /**
    * degree 这里是名称！ 
    */
    @Size(max=100)
    @ApiModelProperty("这里是名称！")
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


    
    public TEdu()
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
