package com.bixuebihui.test.view.dto;

/*
 *  MyTestViewDto
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
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiModelProperty;
import com.sdadas.spring2ts.annotations.SharedModel;

@Api(tags=" select test_gen.*, degree from test_gen left join t_edu on test_gen.edu_id&#x3D;t_edu.id")
@SharedModel("")

public class MyTestViewDto  implements Serializable {


    
    /**
     * id id 
     */
    
    protected Integer  id;

    
    /**
     * name name 
     */
    
    protected String  name;

    
    /**
     * age age 
     */
    
    protected Short  age;

    
    /**
     * birth birth 
     */
    
    protected Timestamp  birth;

    
    /**
     * edu_id edu_id 
     */
    
    protected Integer  eduId;

    
    /**
     * degree degree 
     */
    
    protected String  degree;




    

    /**
     * id id 
     */
    public void setId(Integer id)
    {
      this.id = id;
    }
    /**
     * id id 
     */
    public Integer getId()
    {
      return this.id;
    }
    

    

    /**
     * name name 
     */
    public void setName(String name)
    {
      this.name = name;
    }
    /**
     * name name 
     */
    public String getName()
    {
      return this.name;
    }
    

    

    /**
     * age age 
     */
    public void setAge(Short age)
    {
      this.age = age;
    }
    /**
     * age age 
     */
    public Short getAge()
    {
      return this.age;
    }
    

    

    /**
     * birth birth 
     */
    public void setBirth(Timestamp birth)
    {
      this.birth = birth;
    }
    /**
     * birth birth 
     */
    public Timestamp getBirth()
    {
      return this.birth;
    }
    

    

    /**
     * edu_id edu_id 
     */
    public void setEduId(Integer eduId)
    {
      this.eduId = eduId;
    }
    /**
     * edu_id edu_id 
     */
    public Integer getEduId()
    {
      return this.eduId;
    }
    

    

    /**
     * degree degree 
     */
    public void setDegree(String degree)
    {
      this.degree = degree;
    }
    /**
     * degree degree 
     */
    public String getDegree()
    {
      return this.degree;
    }
    


    
    public MyTestViewDto()
    {
 
        id=0;
         
        name="";
         
        age=0;
         
        birth=new Timestamp(System.currentTimeMillis());
         
        eduId=0;
         
        degree="";
        
    }

    public String toXml()
    {
        StringBuilder s= new StringBuilder();
        String ln = System.getProperty("line.separator");
        s.append("<my_test_view ");

s.append(" id=\"").append(this.getId()).append("\"");
        
s.append(" name=\"").append(StringEscapeUtils.escapeXml11(this.getName())).append("\"");
s.append(" age=\"").append(this.getAge()).append("\"");
        
s.append(" birth=\"").append(this.getBirth()).append("\"");
        
s.append(" eduId=\"").append(this.getEduId()).append("\"");
        
s.append(" degree=\"").append(StringEscapeUtils.escapeXml11(this.getDegree())).append("\"");s.append(" />");
        s.append(ln);
        return s.toString();
    }
}
