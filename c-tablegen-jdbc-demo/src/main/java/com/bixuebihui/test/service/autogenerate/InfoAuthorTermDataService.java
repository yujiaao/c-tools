package com.bixuebihui.test.service.autogenerate;

/*
 *  InfoAuthorTermDataService
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2023
 * email: xwx@live.cn
 */

import com.bixuebihui.query.Paging;

import com.bixuebihui.test.business.InfoAuthorTermDataManager;
import com.bixuebihui.test.cmd.InfoAuthorTermDataSearchCmd;

import com.bixuebihui.test.cmd.InfoAuthorTermDataDelCmd;

import com.bixuebihui.test.dto.InfoAuthorTermDataDto;
import com.bixuebihui.test.pojo.InfoAuthorTermData;
import com.bixuebihui.test.converter.InfoAuthorTermDataConverter;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


public interface InfoAuthorTermDataService {

    /**
     * 查询
     */
    Pair<Integer, List<InfoAuthorTermDataDto>> queryList(Paging<InfoAuthorTermDataSearchCmd> searchCmd);

    
    /**
    * 删除
    */
    Boolean del(InfoAuthorTermDataDelCmd delCmd);
    /**
    * 新增、修改
    */
    InfoAuthorTermDataDto save(InfoAuthorTermDataDto dto);
    

}
