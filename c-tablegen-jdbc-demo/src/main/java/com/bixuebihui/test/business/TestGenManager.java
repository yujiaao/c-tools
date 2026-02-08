package com.bixuebihui.test.business;

/*
 *  TestGenManager
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2023
 * email: xwx@live.cn
 */


import javax.sql.DataSource;
import com.bixuebihui.test.dal.TestGenList;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

import javax.annotation.processing.Generated;

@Generated("com.github.yujiaao:tablegen")
@Repository
public class TestGenManager  extends TestGenList
{
    

    /**
    * @param ds datasource for injecting
    */
    public TestGenManager(@Qualifier("test") DataSource ds)
    {
        super(ds);
    }




    // please write your code here!

}
