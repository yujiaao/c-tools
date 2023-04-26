package com.example.es.business;

/*
 *  ChejiahaoContentsManager
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2022
 * email: xwx@live.cn
 */


import com.example.es.dal.ContentsList;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.annotation.processing.Generated;

import java.util.Map;

@Generated("com.github.yujiaao:tablegen")
@Repository
public class ContentsManager extends ContentsList
{

    /**
     * the ds map must contain host/url, index_name, username and password for ElasticSearch/HTTP connection
     */
    protected ContentsManager(@Qualifier("contentEs") Map<String,String> ds)
    {
       super(ds);
    }

    // please write your code here!

}
