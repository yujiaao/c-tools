package com.bixuebihui.test.cmd;

import java.sql.*;


import io.swagger.annotations.ApiParam;
import lombok.Data;

/*
 *  TestGenSearchCmd
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2022
 * email: xwx@live.cn
 */


/**
* @author xwx
*/
@Data
public class TestGenSearchCmd {



    
        /**
        * id 这里是id! 
        */
        protected Integer  id;
    

    
        /**
        * name 这里是名称！ 
        */
        protected String  name;
    

    
        /**
        * age 这里是年龄 
        */
        protected Short  age;
    

    /**
        * left range of date/time type: birth 这里是日期！ 
        */
        protected Timestamp  begin_birth;

        /**
        * right range of date/time type:  birth 这里是日期！ 
        */
        protected Timestamp  end_birth;

    

    
        /**
        * edu_id 教育程度 
        */
        protected Integer  eduId;
    



}
