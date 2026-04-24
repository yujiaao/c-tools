package com.bixuebihui.test.view.pojo;

/*
 *  MyTestView
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2022
 * email: xwx@live.cn
 */


import java.sql.*;
import java.time.*;
import java.io.Serializable;
import jakarta.validation.constraints.*;
import org.apache.commons.text.StringEscapeUtils;
import javax.annotation.processing.Generated;

@Generated("com.github.yujiaao:tablegen")
public class MyTestView  implements Serializable {


    
    /**
    * id 这里是id! 
    */
    
    protected Integer  id;

    
    /**
    * name 这里是名称！ 
    */
    @Size(max=100)
    protected String  name;

    
    /**
    * age 这里是年龄 
    */
    
    protected Short  age;

    
    /**
    * birth 这里是日期！ 
    */
    
    protected LocalDateTime  birth;

    
    /**
    * edu_id 教育程度 
    */
    
    protected Integer  eduId;

    
    /**
    * gender 性别 
    */
    @Size(max=1)
    protected String  gender;

    
    /**
    * sex 性别 
    */
    
    protected Boolean  sex;

    
    /**
    * child 是否有孩子 
    */
    
    protected Boolean  child;

    
    /**
    * degree 这里是名称！ 
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

    public void setGender(String gender)
    {
      this.gender = gender;
    }
    public String getGender()
    {
      return this.gender;
    }

    public void setSex(Boolean sex)
    {
      this.sex = sex;
    }
    public Boolean getSex()
    {
      return this.sex;
    }

    public void setChild(Boolean child)
    {
      this.child = child;
    }
    public Boolean getChild()
    {
      return this.child;
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
             
            birth=LocalDateTime.now();
             
            eduId=0;
             
            gender="";
             
            sex=Boolean.FALSE;
             
            child=Boolean.FALSE;
             
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
        
        s.append(" gender=\"").append(StringEscapeUtils.escapeXml11(this.getGender())).append("\"");
        s.append(" sex=\"").append(this.getSex()).append("\"");
        
        s.append(" child=\"").append(this.getChild()).append("\"");
        
        s.append(" degree=\"").append(StringEscapeUtils.escapeXml11(this.getDegree())).append("\"");s.append(" />");
        s.append(ln);
        return s.toString();
    }
}
