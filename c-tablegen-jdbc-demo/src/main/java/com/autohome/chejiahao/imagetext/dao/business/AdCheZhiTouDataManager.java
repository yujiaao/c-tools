package com.autohome.chejiahao.imagetext.dao.business;

/*
 *  AdCheZhiTouDataManager
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2022
 * email: xwx@live.cn
 */


import javax.sql.DataSource;
import com.autohome.chejiahao.imagetext.dao.dal.AdCheZhiTouDataList;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.annotation.processing.Generated;

@Generated("com.github.yujiaao:tablegen")
@Repository
public class AdCheZhiTouDataManager  extends AdCheZhiTouDataList
{
    

    /**
    * @param readWrite master datasource for read & write
    * @param readOnly slave datasource for read only
    */
    public AdCheZhiTouDataManager(@Qualifier("imagetext") DataSource readWrite, @Qualifier("imagetext_r") DataSource readOnly)
    {
        super(readWrite,readOnly);
    }
    // please write your code here!

}
