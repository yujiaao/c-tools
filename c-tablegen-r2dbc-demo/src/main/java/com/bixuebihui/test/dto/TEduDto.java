package com.bixuebihui.test.dto;

/*
 *  TEduDto
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2023
 * email: xwx@live.cn
 */


import java.sql.*;
import java.io.Serializable;
import org.apache.commons.text.StringEscapeUtils;

import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.sdadas.spring2ts.annotations.SharedModel;



@SharedModel

public class TEduDto  implements Serializable {



    /**
     * id 这里是id! 
     */
    
    protected Integer  id;


    /**
     * degree 这里是名称！ 
     */
    
    protected String  degree;




    

    /**
     * id 这里是id! 
     */
    public void setId(Integer id)
    {
      this.id = id;
    }
    /**
     * id 这里是id! 
     */
    public Integer getId()
    {
      return this.id;
    }
    

    

    /**
     * degree 这里是名称！ 
     */
    public void setDegree(String degree)
    {
      this.degree = degree;
    }
    /**
     * degree 这里是名称！ 
     */
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
