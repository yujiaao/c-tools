package com.bixuebihui.test.view.service.autogenerate;

/*
 *  MyTestViewService
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2023
 * email: xwx@live.cn
 */

import com.bixuebihui.query.Paging;

import com.bixuebihui.test.view.business.MyTestViewManager;
import com.bixuebihui.test.view.cmd.MyTestViewSearchCmd;

import com.bixuebihui.test.view.dto.MyTestViewDto;
import com.bixuebihui.test.view.pojo.MyTestView;
import com.bixuebihui.test.view.converter.MyTestViewConverter;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


public interface MyTestViewService {

    /**
     * 查询
     */
    Pair<Integer, List<MyTestViewDto>> queryList(Paging<MyTestViewSearchCmd> searchCmd);

    

}
