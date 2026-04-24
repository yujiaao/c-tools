package com.bixuebihui.test.view.cmd;

import java.sql.*;
import java.time.*;



import jakarta.validation.constraints.*;

/*
 *  MyTestViewSearchCmd
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2022
 * email: xwx@live.cn
 */


/**
* @author xwx
* https://stackoverflow.com/questions/26612404/spring-map-get-request-parameters-to-pojo-automatically/26612778#26612778
*/
public class MyTestViewSearchCmd {



    
        /**
        * id 这里是id! 
        *  change type of Integer to List<Integer> will generate 'in' sql condition.
        */
        
        protected Integer  id;
    
    
        /**
        * name 这里是名称！ 
        *  change type of String to List<String> will generate 'in' sql condition.
        */
        
        protected String  name;
    
    
        /**
        * age 这里是年龄 
        *  change type of Short to List<Short> will generate 'in' sql condition.
        */
        
        protected Short  age;
    
    
        /**
        * birth 这里是日期！ 
        *  change type of LocalDateTime to List<LocalDateTime> will generate 'in' sql condition.
        */
        
        protected LocalDateTime  birth;
    
    
        /**
        * edu_id 教育程度 
        *  change type of Integer to List<Integer> will generate 'in' sql condition.
        */
        
        protected Integer  eduId;
    
    
        /**
        * gender 性别 
        *  change type of String to List<String> will generate 'in' sql condition.
        */
        
        protected String  gender;
    
    
        /**
        * sex 性别 
        *  change type of Boolean to List<Boolean> will generate 'in' sql condition.
        */
        
        protected Boolean  sex;
    
    
        /**
        * child 是否有孩子 
        *  change type of Boolean to List<Boolean> will generate 'in' sql condition.
        */
        
        protected Boolean  child;
    
    
        /**
        * degree 这里是名称！ 
        *  change type of String to List<String> will generate 'in' sql condition.
        */
        
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



}
