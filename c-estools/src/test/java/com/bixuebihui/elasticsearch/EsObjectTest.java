package com.bixuebihui.elasticsearch;

import com.bixuebihui.elasticsearch.query.Query;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static java.util.Collections.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public  class EsObjectTest {

    @Test
    public void testMatchAll() {

        String s = EsQueryBuilder.build(Query.match_all(), 0, 5,
                emptySet(),emptySet(), null, null, emptyMap(), null, emptyList());

        assertEquals("{\"size\":5,\"query\":{\"match_all\":{}}}", s);
    }


    @Test
    public void testMatchAllWithSort(){
        LinkedHashMap<String, String> sort =new LinkedHashMap<>();
        sort.put("age", "desc");


        String s = EsQueryBuilder.build(Query.match_all()
               , 0,5,emptySet(), emptySet(), sort, null, emptyMap(), null, emptyList());

        assertEquals("{\"size\":5,\"query\":{\"match_all\":{}},\"sort\":{\"age\":\"desc\"}}", s);

        /**
         * GET bank/_search
         * {
         *   "query": {"match_all": {}},
         *   "sort": [
         *     {
         *       "account_number": "desc"
         *     },
         *     {
         *       "balance": "desc"
         *     }
         *   ]
         * }
         */

    }

    @Test
    public void testMatch() {

        String s = EsQueryBuilder.build(Query.match(null, null)
                .setFieldQuery("abc","test 123"), 0,5,emptySet(), emptySet(),
                null, null, emptyMap(), null, emptyList());

        assertEquals("{\"size\":5,\"query\":{\"match\":{\"abc\":{\"query\":\"test 123\"}}}}", s);
    }



    @Test
    public void testMatchWithSort() {


        LinkedHashMap<String, String> sort =new LinkedHashMap<>();
        sort.put("age", "desc");


        String s = EsQueryBuilder.build(Query.match(null, null)
                .setFieldQuery("abc","test 123"), 0,5,emptySet(),emptySet(),
                sort, null, emptyMap(), null, emptyList());

        assertEquals("{\"size\":5,\"query\":{\"match\":{\"abc\":{\"query\":\"test 123\"}}},\"sort\":{\"age\":\"desc\"}}", s);
    }

}
