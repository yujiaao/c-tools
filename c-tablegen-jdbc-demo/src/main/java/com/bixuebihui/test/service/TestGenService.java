package com.bixuebihui.test.service;

/*
 *  TestGenService
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2022
 * email: xwx@live.cn
 */

import com.bixuebihui.query.Paging;

import com.bixuebihui.test.business.TestGenManager;
import com.bixuebihui.test.cmd.TestGenSearchCmd;
import com.bixuebihui.test.dto.TestGenDto;
import com.bixuebihui.test.pojo.TestGen;
import com.bixuebihui.test.converters.TestGenConverter;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.processing.Generated;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Generated("com.github.yujiaao:tablegen")
@Service
public class TestGenService {
    @Autowired
    TestGenManager testGenManager;


    public Pair<Integer, List<TestGenDto>> queryList(Paging<TestGenSearchCmd> searchCmd) {
        List<TestGen> infos = testGenManager.ar()
                    .query(searchCmd).findAll();

        List<TestGenDto> collect = infos.stream()
                    .map(TestGenConverter.MAPPER::toDto).collect(toList());

        int count = testGenManager.ar()
                    .query(searchCmd).count();


        // do some thing

        return Pair.of(count, collect);
    }



}
