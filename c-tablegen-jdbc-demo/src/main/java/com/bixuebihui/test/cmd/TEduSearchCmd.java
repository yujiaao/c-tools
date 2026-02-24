package com.bixuebihui.test.cmd;


import io.swagger.annotations.ApiModelProperty;

/*
 *  TEduSearchCmd
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
public class TEduSearchCmd {



    
        /**
        * id 这里是id! 
        *  change type of Integer to List<Integer> will generate 'in' sql condition.
        */
        @ApiModelProperty("这里是id!")
        protected Integer  id;
    
    
        /**
        * degree 这里是名称！ 
        *  change type of String to List<String> will generate 'in' sql condition.
        */
        @ApiModelProperty("这里是名称！")
        protected String  degree;
    



    public void setId(Integer id)
    {
        this.id = id;
    }
    public Integer getId()
    {
        return this.id;
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
