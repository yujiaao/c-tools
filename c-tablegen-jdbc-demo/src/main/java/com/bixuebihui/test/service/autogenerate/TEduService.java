package com.bixuebihui.test.service.autogenerate;

/*
 *  TEduService
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2023
 * email: xwx@live.cn
 */

import com.bixuebihui.query.Paging;

import com.bixuebihui.test.business.TEduManager;
import com.bixuebihui.test.cmd.TEduSearchCmd;

import com.bixuebihui.test.cmd.TEduDelCmd;

import com.bixuebihui.test.dto.TEduDto;
import com.bixuebihui.test.pojo.TEdu;
import com.bixuebihui.test.converter.TEduConverter;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


public interface TEduService {

    /**
     * 查询
     */
    Pair<Integer, List<TEduDto>> queryList(Paging<TEduSearchCmd> searchCmd);

    
    /**
    * 删除
    */
    Boolean del(TEduDelCmd delCmd);
    /**
    * 新增、修改
    */
    TEduDto save(TEduDto dto);
    

}
