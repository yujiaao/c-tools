package com.bixuebihui.test.service;

/*
 *  TEduService
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2022
 * email: xwx@live.cn
 */

import com.bixuebihui.query.Paging;

import com.bixuebihui.test.business.TEduManager;
import com.bixuebihui.test.cmd.TEduSearchCmd;
import com.bixuebihui.test.dto.TEduDto;
import com.bixuebihui.test.pojo.TEdu;
import com.bixuebihui.test.converters.TEduConverter;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.processing.Generated;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Generated("com.github.yujiaao:tablegen")
@Service
public class TEduService {
    @Autowired
    TEduManager tEduManager;


    public Pair<Integer, List<TEduDto>> queryList(Paging<TEduSearchCmd> searchCmd) {
        List<TEdu> infos = tEduManager.ar()
                    .query(searchCmd).findAll();

        List<TEduDto> collect = infos.stream()
                    .map(TEduConverter.MAPPER::toDto).collect(toList());

        int count = tEduManager.ar()
                    .query(searchCmd).count();


        // do some thing

        return Pair.of(count, collect);
    }



}
