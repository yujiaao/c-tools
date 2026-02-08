package com.bixuebihui.test;

/*
 *  BaseList
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2023
 * email: xwx@live.cn
 */


import com.bixuebihui.jdbc.BaseDao;
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
        super(ds,ds);
    }
}
