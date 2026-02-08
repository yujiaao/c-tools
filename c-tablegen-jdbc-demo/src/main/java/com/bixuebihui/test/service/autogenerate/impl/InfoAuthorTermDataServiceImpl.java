package com.bixuebihui.test.service.autogenerate.impl;

/*
 *  InfoAuthorTermDataServiceImpl
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

import com.bixuebihui.test.converter.InfoAuthorTermDataConverter;
import com.bixuebihui.test.dto.InfoAuthorTermDataDto;
import com.bixuebihui.test.pojo.InfoAuthorTermData;
import com.bixuebihui.test.service.autogenerate.InfoAuthorTermDataService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

import javax.annotation.processing.Generated;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Generated("com.github.yujiaao:tablegen")
@Service
public class InfoAuthorTermDataServiceImpl implements  InfoAuthorTermDataService{
    @Autowired
    InfoAuthorTermDataManager infoAuthorTermDataManager;

    @Override
    public Pair<Integer, List<InfoAuthorTermDataDto>> queryList(Paging<InfoAuthorTermDataSearchCmd> searchCmd) {
        Pair<Long, List<InfoAuthorTermData>>  infos = infoAuthorTermDataManager.ar()
                    .query(searchCmd).findAllWithCount();

        List<InfoAuthorTermDataDto> collect = InfoAuthorTermDataConverter.MAPPER.toDtoList(infos.getRight());

        return Pair.of(infos.getLeft().intValue(), collect);
    }

    @Override
    public Boolean del(InfoAuthorTermDataDelCmd delCmd) {

    return false;

    }

    @Override
    public InfoAuthorTermDataDto save(InfoAuthorTermDataDto dto)
    {
        InfoAuthorTermData info = InfoAuthorTermDataConverter.MAPPER.toDao(dto);
        infoAuthorTermDataManager.save(info);
        return dto;
    }


}
