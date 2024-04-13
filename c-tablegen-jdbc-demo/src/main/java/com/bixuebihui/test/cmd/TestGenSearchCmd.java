package com.bixuebihui.test.cmd;

import java.sql.*;



import io.swagger.annotations.ApiModelProperty;


import jakarta.validation.constraints.*;

/*
 *  TestGenSearchCmd
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2023
 * email: xwx@live.cn
 */


/**
* @author xwx
* https://stackoverflow.com/questions/26612404/spring-map-get-request-parameters-to-pojo-automatically/26612778#26612778
*/
public class TestGenSearchCmd {



    
        /**
        * id 这里是id! 
        *  change type of Integer to List<Integer> will generate 'in' sql condition.
        */
        @ApiModelProperty("这里是id!")
        protected Integer  id;
    
    
        /**
        * name 这里是名称！ 
    * NotNull, default value: NULL
        *  change type of String to List<String> will generate 'in' sql condition.
        */
        @ApiModelProperty("这里是名称！")
        protected String  name;
    
    
        /**
        * age 这里是年龄 
    * NotNull, default value: NULL
        *  change type of Short to List<Short> will generate 'in' sql condition.
        */
        @ApiModelProperty("这里是年龄")
        protected Short  age;
    
    /**
        * left range of date/time type: birth 这里是日期！ 
    * NotNull, default value: NULL
        */
        protected Timestamp  begin_birth;

        /**
        * right range of date/time type:  birth 这里是日期！ 
    * NotNull, default value: NULL
        */
        protected Timestamp  end_birth;

    
    
        /**
        * edu_id 教育程度 
    * NotNull, default value: NULL
        *  change type of Integer to List<Integer> will generate 'in' sql condition.
        */
        @ApiModelProperty("教育程度")
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



    public void setBegin_birth(Timestamp birth)
    {
        this.begin_birth = birth;
    }
    public Timestamp getBegin_birth()
    {
        return this.begin_birth;
    }
    public void setEnd_birth(Timestamp birth)
    {
        this.end_birth = birth;
    }
    public Timestamp getEnd_birth()
    {
        return this.end_birth;
    }




    public void setEduId(Integer eduId)
    {
        this.eduId = eduId;
    }
    public Integer getEduId()
    {
        return this.eduId;
    }




}
