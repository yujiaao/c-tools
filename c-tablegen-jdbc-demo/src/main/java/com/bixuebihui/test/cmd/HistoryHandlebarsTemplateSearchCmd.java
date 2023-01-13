package com.bixuebihui.test.cmd;

import java.sql.*;


import io.swagger.annotations.ApiParam;
import lombok.Data;

/*
 *  HistoryHandlebarsTemplateSearchCmd
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
public class HistoryHandlebarsTemplateSearchCmd {



    
        /**
        * history_id  
        */
        protected Integer  historyId;
    

    
        /**
        * template_id 模板ID 
        */
        protected Integer  templateId;
    

    
        /**
        * template_code 模板code,需唯一 
        */
        protected String  templateCode;
    

    
        /**
        * description 模板说明 
        */
        protected String  description;
    

    
        /**
        * datasource_id json数据源id 
        */
        protected Integer  datasourceId;
    

    
        /**
        * base_template_id 继承模板id 
        */
        protected Integer  baseTemplateId;
    

    
        /**
        * template_body 模板主体 
        */
        protected String  templateBody;
    

    
        /**
        * operator_id 操作用户id 
        */
        protected Integer  operatorId;
    

    
        /**
        * version 版本号 
    * NotNull, default value: 0
        */
        protected Integer  version;
    

    
        /**
        * is_del 0：未删除；1：已删除 
    * NotNull, default value: 0
        */
        protected Integer  isDel;
    

    /**
        * left range of date/time type: created_stime 数据创建时间 
    * NotNull, default value: current_timestamp()
        */
        protected Timestamp  begin_createdStime;

        /**
        * right range of date/time type:  created_stime 数据创建时间 
    * NotNull, default value: current_timestamp()
        */
        protected Timestamp  end_createdStime;

    

    /**
        * left range of date/time type: modified_stime 数据更新时间 
    * NotNull, default value: current_timestamp()
        */
        protected Timestamp  begin_modifiedStime;

        /**
        * right range of date/time type:  modified_stime 数据更新时间 
    * NotNull, default value: current_timestamp()
        */
        protected Timestamp  end_modifiedStime;

    



}
