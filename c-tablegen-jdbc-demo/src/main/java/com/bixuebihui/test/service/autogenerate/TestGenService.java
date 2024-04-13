package com.bixuebihui.test.service.autogenerate;

/*
 *  TestGenService
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2023
 * email: xwx@live.cn
 */

import com.bixuebihui.query.Paging;

import com.bixuebihui.test.business.TestGenManager;
import com.bixuebihui.test.cmd.TestGenSearchCmd;

import com.bixuebihui.test.cmd.TestGenDelCmd;

import com.bixuebihui.test.dto.TestGenDto;
import com.bixuebihui.test.pojo.TestGen;
import com.bixuebihui.test.converter.TestGenConverter;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


public interface TestGenService {

    /**
     * 查询
     */
    Pair<Integer, List<TestGenDto>> queryList(Paging<TestGenSearchCmd> searchCmd);

    
    /**
    * 删除
    */
    Boolean del(TestGenDelCmd delCmd);
    /**
    * 新增、修改
    */
    TestGenDto save(TestGenDto dto);
    

}
