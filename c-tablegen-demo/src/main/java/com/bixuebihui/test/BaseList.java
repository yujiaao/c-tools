package com.bixuebihui.test;

/*
 *  BaseList
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2021
 * email: www@qsn.so
 */


import com.bixuebihui.jdbc.BaseDao;
import com.bixuebihui.jdbc.IDbHelper;
import com.bixuebihui.jdbc.MSDbHelper;
import com.bixuebihui.jdbc.aop.DbHelperAroundAdvice;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.processing.Generated;

import javax.sql.DataSource;

/**
* @author xwx
*/

@Generated("com.github.yujiaao:tablegen")
@Service
public abstract class BaseList<T, V> extends BaseDao<T, V> {

    public BaseList(DataSource ds) {

        MSDbHelper dbHelper0 = new MSDbHelper();
        dbHelper0.setMasterDatasource(ds);
        dbHelper0.setDataSource(ds);

        if (LOG.isDebugEnabled()) {
            ProxyFactory obj = new ProxyFactory(dbHelper0);
            obj.addAdvice(new DbHelperAroundAdvice());
            dbHelper = (IDbHelper) obj.getProxy();
        } else {
            dbHelper = dbHelper0;
        }

    }

}
