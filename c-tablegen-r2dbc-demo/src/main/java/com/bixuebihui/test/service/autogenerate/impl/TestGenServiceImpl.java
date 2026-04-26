package com.bixuebihui.test.service.autogenerate.impl;

/*
 *  TestGenServiceImpl
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

import com.bixuebihui.test.converter.TestGenConverter;
import com.bixuebihui.test.dto.TestGenDto;
import com.bixuebihui.test.pojo.TestGen;
import com.bixuebihui.test.service.autogenerate.TestGenService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

import javax.annotation.processing.Generated;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Generated("com.github.yujiaao:tablegen")
@Service
public class TestGenServiceImpl implements  TestGenService{
    @Autowired
    TestGenManager testGenManager; 

    @Override
    public Pair<Integer, List<TestGenDto>> queryList(Paging<TestGenSearchCmd> searchCmd) {
        int start = searchCmd.getStart() == null ? 0 : searchCmd.getStart().intValue();
        int count = searchCmd.getCount() == null ? 10 : searchCmd.getCount().intValue();
        List<TestGen> records = testGenManager.ar().limit(start, count).findAll().block();
        Integer total = testGenManager.ar().count().block();
        Pair<Long, List<TestGen>> infos = Pair.of(
                total == null ? 0L : total.longValue(),
                records == null ? java.util.Collections.emptyList() : records
        );

        List<TestGenDto> collect = TestGenConverter.MAPPER.toDtoList(infos.getRight());

        return Pair.of(infos.getLeft().intValue(), collect);
    }

    @Override
    public Boolean del(TestGenDelCmd delCmd) {

    
        return testGenManager.deleteByKeys(delCmd.getIds()
                .toArray(new Integer[0])).block();
    
    }

    @Override
    public TestGenDto save(TestGenDto dto)
    {
        TestGen info = TestGenConverter.MAPPER.toDao(dto);
        testGenManager.save(info);
        return TestGenConverter.MAPPER.toDto(info);
    }


}
