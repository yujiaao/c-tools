package com.bixuebihui.test.business;

/*
 *  TestGenManager
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2021
 * email: www@qsn.so
 */


import com.bixuebihui.test.pojo.TestGen;
import io.r2dbc.spi.ConnectionFactory;
import com.bixuebihui.test.dal.TestGenList;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.processing.Generated;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Generated("com.github.yujiaao:tablegen")
@Repository
public class TestGenManager  extends TestGenList
{

    /**
     * @param ds datasource for injecting
     */
    public TestGenManager(@Qualifier("test") ConnectionFactory ds)
    {
        super(ds);
    }


    public Mono<List<Long>> getIds(){
        return this.ar().greaterOrEqualThan(F.ID,2).limit(0,2).getLongVector(F.ID);
    }

    public Mono<Integer> batchInsert(){
        return Flux.fromIterable(Arrays.asList("1,2,3".split(",")))
                .flatMap(x -> {
                    TestGen testGen = new TestGen();
                    testGen.setAge(Short.parseShort(x));
                    testGen.setEduId(1);
                    testGen.setName("name"+x);
                    testGen.setBirth(new Timestamp(System.currentTimeMillis()));
                    return this.insertAutoIncrement(testGen);

                }).map(x -> (x ? 1 : 0)).reduce((x, y) -> x + y);
    }

    public Mono<Integer> batchInsertAutoNewKey(){
        return Flux.fromIterable(Arrays.asList("1,2,3".split(",")))
                .flatMap(x -> {
                    TestGen testGen = new TestGen();
                    testGen.setAge(Short.parseShort(x));
                    testGen.setEduId(1);
                    testGen.setName("name"+x);
                    testGen.setBirth(new Timestamp(System.currentTimeMillis()));
                    return this.insertAutoNewKey(testGen);

                }).map(x -> (x ? 1 : 0)).reduce((x, y) -> x + y);
    }
}
