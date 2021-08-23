package com.bixuebihui.test.view.pojo;

/*
 *  MyTestView
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
@Schema(description =" select test_gen.*, degree from test_gen left join t_edu on test_gen.edu_id&#x3D;t_edu.id")
public class MyTestView  implements Serializable {


    
    /**
    * id
    */
    
    protected Integer  id;

    
    /**
    * name
    */
    @Size(max=100)
    protected String  name;

    
    /**
    * age
    */
    
    protected Short  age;

    
    /**
    * birth
    */
    
    protected Timestamp  birth;

    
    /**
    * edu_id
    */
    
    protected Integer  eduId;

    
    /**
    * degree
    */
    @Size(max=100)
    protected String  degree;




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

    public void setBirth(Timestamp birth)
    {
      this.birth = birth;
    }
    public Timestamp getBirth()
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

    public void setDegree(String degree)
    {
      this.degree = degree;
    }
    public String getDegree()
    {
      return this.degree;
    }


    
    public MyTestView()
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
