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
import org.apache.commons.text.StringEscapeUtils;

import javax.annotation.processing.Generated;




import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import com.sdadas.spring2ts.annotations.SharedModel;

@Api(tags="my_test_view")
@SharedModel("")

public class MyTestViewDto  implements Serializable {


    
    /**
     * edu_id edu_id 
     */
    
    protected Integer  eduId;




    

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
    


    
    public MyTestViewDto()
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
