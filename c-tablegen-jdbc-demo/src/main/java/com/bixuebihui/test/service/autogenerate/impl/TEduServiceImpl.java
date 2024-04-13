package com.bixuebihui.test.service.autogenerate.impl;

/*
 *  TEduServiceImpl
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

import com.bixuebihui.test.converter.TEduConverter;
import com.bixuebihui.test.dto.TEduDto;
import com.bixuebihui.test.pojo.TEdu;
import com.bixuebihui.test.service.autogenerate.TEduService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

import javax.annotation.processing.Generated;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Generated("com.github.yujiaao:tablegen")
@Service
public class TEduServiceImpl implements  TEduService{
    @Autowired
    TEduManager tEduManager;

    @Override
    public Pair<Integer, List<TEduDto>> queryList(Paging<TEduSearchCmd> searchCmd) {
        Pair<Long, List<TEdu>>  infos = tEduManager.ar()
                    .query(searchCmd).findAllWithCount();

        List<TEduDto> collect = TEduConverter.MAPPER.toDtoList(infos.getRight());

        return Pair.of(infos.getLeft().intValue(), collect);
    }

    @Override
    public Boolean del(TEduDelCmd delCmd) {

    
        return tEduManager.deleteByKeys(delCmd.getIds()
                .toArray(new Integer[0]));
    
    }

    @Override
    public TEduDto save(TEduDto dto)
    {
        TEdu info = TEduConverter.MAPPER.toDao(dto);
        tEduManager.save(info);
        return dto;
    }


}
