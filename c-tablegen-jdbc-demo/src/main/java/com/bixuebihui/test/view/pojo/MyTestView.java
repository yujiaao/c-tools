package com.bixuebihui.test.view.pojo;

/*
 *  MyTestView
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

@Api(tags="my_test_view")


@Generated("com.github.yujiaao:tablegen")
public class MyTestView  implements Serializable {


    
    /**
    * edu_id edu_id 
    */
    
    protected Integer  eduId;




    public void setEduId(Integer eduId)
    {
      this.eduId = eduId;
    }
    public Integer getEduId()
    {
      return this.eduId;
    }


    
    public MyTestView()
    {
     
            eduId=0;
            
    }

    public String toXml()
    {
        StringBuilder s= new StringBuilder();
        String ln = System.getProperty("line.separator");
        s.append("<my_test_view ");

        s.append(" eduId=\"").append(this.getEduId()).append("\"");
        s.append(" />");
        s.append(ln);
        return s.toString();
    }
}
