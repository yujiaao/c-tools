package com.bixuebihui.cache;
/**
 * BaseList
 * <p>
 * WARNING! Automatically generated file!
 * Do not edit!
 * Code Generator by J.A.Carter
 * Modified by Xing Wanxiang 2008
 * (c) www.goldjetty.com
 */

import com.bixuebihui.BeanFactory;
import com.bixuebihui.jdbc.BaseDao;
import com.bixuebihui.jdbc.IDbHelper;

/**
 * @author xwx
 */
public abstract class BaseList<T, V> extends BaseDao<T, V> {
    public BaseList() {
        dbHelper = (IDbHelper) BeanFactory.createObjectById("cacheDbHelper");
    }
}
