package com.autohome.chejiahao.imagetext.dao.view.business;

/*
 *  NeteasyVideoAuditingViewManager
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2022
 * email: xwx@live.cn
 */


import javax.sql.DataSource;
import com.autohome.chejiahao.imagetext.dao.view.dal.NeteasyVideoAuditingViewList;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.annotation.processing.Generated;

@Generated("com.github.yujiaao:tablegen")
@Repository
public class NeteasyVideoAuditingViewManager  extends NeteasyVideoAuditingViewList
{
    

    /**
    * @param readWrite master datasource for read & write
    * @param readOnly slave datasource for read only
    */
    public NeteasyVideoAuditingViewManager(@Qualifier("imagetext") DataSource readWrite, @Qualifier("imagetext_r") DataSource readOnly)
    {
        super(readWrite,readOnly);
    }
    // please write your code here!

}
