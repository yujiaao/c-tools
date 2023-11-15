package com.bixuebihui.test.pojo;

/*
 *  TestGen
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
import javax.validation.constraints.*;
import org.apache.commons.text.StringEscapeUtils;


import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.processing.Generated;

@Generated("com.github.yujiaao:tablegen")
@Schema(description ="测试表")
public class TestGen  implements Serializable {


    
    /**
    * id
    */
    @Schema(title = "这里是id!")
    protected Integer  id;

    
    /**
    * name
    */
    @Size(max=100)
@Schema(title = "这里是名称！")
    protected String  name;

    
    /**
    * age
    */
    @Schema(title = "这里是年龄")
    protected Short  age;

    
    /**
    * birth
    */
    @Schema(title = "这里是日期！")
    protected LocalDateTime  birth;

    
    /**
    * edu_id
    */
    @Schema(title = "教育程度")
    protected Integer  eduId;




    public void setId(Integer id)
    {
      this.id = id;
    }
    public Integer getId()
    {
      return this.id;
    }

    public void setName(String name)
    {
      this.name = name;
    }
    public String getName()
    {
      return this.name;
    }

    public void setAge(Short age)
    {
      this.age = age;
    }
    public Short getAge()
    {
      return this.age;
    }

    public void setBirth(LocalDateTime birth)
    {
      this.birth = birth;
    }
    public LocalDateTime getBirth()
    {
      return this.birth;
    }

    public void setEduId(Integer eduId)
    {
      this.eduId = eduId;
    }
    public Integer getEduId()
    {
      return this.eduId;
    }


    
    public TestGen()
    {
     
            id=0;
             
            name="";
             
            age=0;
             
            birth=LocalDateTime.now();
             
            eduId=0;
            
    }

    public String toXml()
    {
        StringBuilder s= new StringBuilder();
        String ln = System.getProperty("line.separator");
        s.append("<test_gen ");

        s.append(" id=\"").append(this.getId()).append("\"");
        
        s.append(" name=\"").append(StringEscapeUtils.escapeXml11(this.getName())).append("\"");
        s.append(" age=\"").append(this.getAge()).append("\"");
        
        s.append(" birth=\"").append(this.getBirth()).append("\"");
        
        s.append(" eduId=\"").append(this.getEduId()).append("\"");
        s.append(" />");
        s.append(ln);
        return s.toString();
    }
}
