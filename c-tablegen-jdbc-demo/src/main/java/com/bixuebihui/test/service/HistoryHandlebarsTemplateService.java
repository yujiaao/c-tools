package com.bixuebihui.test.service;

/*
 *  HistoryHandlebarsTemplateService
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2022
 * email: xwx@live.cn
 */

import com.bixuebihui.query.Paging;

import com.bixuebihui.test.business.HistoryHandlebarsTemplateManager;
import com.bixuebihui.test.cmd.HistoryHandlebarsTemplateSearchCmd;
import com.bixuebihui.test.dto.HistoryHandlebarsTemplateDto;
import com.bixuebihui.test.pojo.HistoryHandlebarsTemplate;
import com.bixuebihui.test.converters.HistoryHandlebarsTemplateConverter;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.processing.Generated;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Generated("com.github.yujiaao:tablegen")
@Service
public class HistoryHandlebarsTemplateService {
    @Autowired
    HistoryHandlebarsTemplateManager historyHandlebarsTemplateManager;


    public Pair<Integer, List<HistoryHandlebarsTemplateDto>> queryList(Paging<HistoryHandlebarsTemplateSearchCmd> searchCmd) {
        List<HistoryHandlebarsTemplate> infos = historyHandlebarsTemplateManager.ar()
                    .query(searchCmd).findAll();

        List<HistoryHandlebarsTemplateDto> collect = infos.stream()
                    .map(HistoryHandlebarsTemplateConverter.MAPPER::toDto).collect(toList());

        int count = historyHandlebarsTemplateManager.ar()
                    .query(searchCmd).count();


        // do some thing

        return Pair.of(count, collect);
    }



}
