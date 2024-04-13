package com.bixuebihui.test.view.service.autogenerate.impl;

/*
 *  MyTestViewServiceImpl
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

import com.bixuebihui.test.view.converter.MyTestViewConverter;
import com.bixuebihui.test.view.dto.MyTestViewDto;
import com.bixuebihui.test.view.pojo.MyTestView;
import com.bixuebihui.test.view.service.autogenerate.MyTestViewService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

import javax.annotation.processing.Generated;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Generated("com.github.yujiaao:tablegen")
@Service
public class MyTestViewServiceImpl implements  MyTestViewService{
    @Autowired
    MyTestViewManager myTestViewManager;

    @Override
    public Pair<Integer, List<MyTestViewDto>> queryList(Paging<MyTestViewSearchCmd> searchCmd) {
        Pair<Long, List<MyTestView>>  infos = myTestViewManager.ar()
                    .query(searchCmd).findAllWithCount();

        List<MyTestViewDto> collect = MyTestViewConverter.MAPPER.toDtoList(infos.getRight());

        return Pair.of(infos.getLeft().intValue(), collect);
    }


}
