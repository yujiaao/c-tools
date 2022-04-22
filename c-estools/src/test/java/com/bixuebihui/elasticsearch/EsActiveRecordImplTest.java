package com.bixuebihui.elasticsearch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EsActiveRecordImplTest {
    String indexName = "my-index-000001";

    private EsActiveRecordImpl<Object, Long> ar = new EsActiveRecordImpl<>(
            new EsOperator<>( new EsRequest("http://localhost:9200","","", indexName)) {
                @Override
                protected Class<Object> getType() {
                    return Object.class;
                }
            });

    @BeforeEach
    void setUp() {

    }


    @Test
    void esActiveRecordImpl() {
        new EsActiveRecordImpl<>();
    }


    @Test
    void getResultFields() {
    }

    @Test
    void getResultFieldsSet() {
    }

    @Test
    void in() {
        Object res = ar.in("abc", new Object[]{1, 2, 3}).find();
        assertTrue(res==null || res.toString().contains("abc"));
    }

    @Test
    void emptyStringAsNullCondition() {
    }

    @Test
    void testIn() {

    }

    @Test
    void like() {
    }

    @Test
    void startWith() {
    }

    @Test
    void eq() {
        List<Object> res = ar.eq("abc", 2).findAll();
        System.out.println(res);
    }

    @Test
    void between() {
    }

    @Test
    void notBetween() {
    }

    @Test
    void ne() {
    }

    @Test
    void testEq() {
    }

    @Test
    void greaterThan() {
    }

    @Test
    void smallerThan() {
    }

    @Test
    void isNull() {
    }

    @Test
    void isNotNull() {
    }

    @Test
    void greaterOrEqualThan() {
    }

    @Test
    void smallerOrEqualThan() {
    }

    @Test
    void order() {
    }

    @Test
    void orderByIn() {
    }

    @Test
    void limit() {
    }

    @Test
    void findAll() {
    }

    @Test
    void getParams() {
    }

    @Test
    void testFindAll() {
    }

    @Test
    void formWhereClause() {
    }

    @Test
    void find() {
    }

    @Test
    void delete() {
    }

    @Test
    void get() {
    }

    @Test
    void getVector() {
    }

    @Test
    void getLongVector() {
    }

    @Test
    void parseLimitSql() {
    }

    @Test
    void getSql() {
    }

    @Test
    void parseOrder() {
    }

    @Test
    void parseGroupBy() {
    }

    @Test
    void asc() {
    }

    @Test
    void desc() {
    }

    @Test
    void last() {
    }

    @Test
    void getCondStack() {
    }

    @Test
    void or() {
    }

    @Test
    void alias() {
    }

    @Test
    void fields() {
    }

    @Test
    void join() {
    }

    @Test
    void groupBy() {
    }

    @Test
    void having() {
    }

    @Test
    void count() {
    }

    @Test
    void countValue() {
    }

    @Test
    void exists() {
    }

    @Test
    void update() {
        String _id="12345";
        String field = "abc";
        String value ="123";
        int res = ar.eq("abc", 2).update(_id, field, value);
        System.out.println(res);
    }
}
