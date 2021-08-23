package com.bixuebihui.test.view.business;

/*
 *  MyTestViewManager
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2021
 * email: www@qsn.so
 */


import javax.sql.DataSource;
import com.bixuebihui.test.view.dal.MyTestViewList;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.annotation.processing.Generated;

@Generated("com.github.yujiaao:tablegen")
@Repository
public class MyTestViewManager  extends MyTestViewList
{
    
    /**
     * @param ds datasource for injecting
     */
    public MyTestViewManager(@Qualifier("test") DataSource ds)
    {
        super(ds);
    }

    // please write your code here!

}
