package com.bixuebihui.test.dto;

/*
 *  TestGenDto
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

@Api(tags="测试表")
@SharedModel("")

public class TestGenDto  implements Serializable {


    
    /**
     * id 这里是id! 
     */
    @ApiModelProperty("这里是id!")
    protected Integer  id;

    
    /**
     * name 这里是名称！ 
    * NotNull, default value: NULL
     */
    @ApiModelProperty("这里是名称！")
    protected String  name;

    
    /**
     * age 这里是年龄 
    * NotNull, default value: NULL
     */
    @ApiModelProperty("这里是年龄")
    protected Short  age;

    
    /**
     * birth 这里是日期！ 
    * NotNull, default value: NULL
     */
    @ApiModelProperty("这里是日期！")
    protected Timestamp  birth;

    
    /**
     * edu_id 教育程度 
    * NotNull, default value: NULL
     */
    @ApiModelProperty("教育程度")
    protected Integer  eduId;




    

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
     * name 这里是名称！ 
    * NotNull, default value: NULL
     */
    public void setName(String name)
    {
      this.name = name;
    }
    /**
     * name 这里是名称！ 
    * NotNull, default value: NULL
     */
    public String getName()
    {
      return this.name;
    }
    

    

    /**
     * age 这里是年龄 
    * NotNull, default value: NULL
     */
    public void setAge(Short age)
    {
      this.age = age;
    }
    /**
     * age 这里是年龄 
    * NotNull, default value: NULL
     */
    public Short getAge()
    {
      return this.age;
    }
    

    

    /**
     * birth 这里是日期！ 
    * NotNull, default value: NULL
     */
    public void setBirth(Timestamp birth)
    {
      this.birth = birth;
    }
    /**
     * birth 这里是日期！ 
    * NotNull, default value: NULL
     */
    public Timestamp getBirth()
    {
      return this.birth;
    }
    

    

    /**
     * edu_id 教育程度 
    * NotNull, default value: NULL
     */
    public void setEduId(Integer eduId)
    {
      this.eduId = eduId;
    }
    /**
     * edu_id 教育程度 
    * NotNull, default value: NULL
     */
    public Integer getEduId()
    {
      return this.eduId;
    }
    


    
    public TestGenDto()
    {
 
        id=0;
         
        name="";
         
        age=0;
         
        birth=new Timestamp(System.currentTimeMillis());
         
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
