package com.bixuebihui.jmesa.elasticsearch;

import com.bixuebihui.jmesa.elasticsearch.processor.Sort;
import com.bixuebihui.jmesa.elasticsearch.query.Query;
import org.apache.commons.compress.utils.Lists;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public  class EsObjectTest {

    @Test
    public void testMatchAll() {

        String s = EsQueryBuilder.build(Query.match_all(), 0, 5, "",null);

        assertEquals("{\"size\":5,\"query\":{\"match_all\":{}}}", s);
    }


    @Test
    public void testMatchAllWithSort(){
        LinkedHashMap<String, String> sort =new LinkedHashMap<>();
        sort.put("age", "desc");


        String s = EsQueryBuilder.build(Query.match_all()
               , 0,5,"", sort);

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
                .setFieldQuery("abc","test 123"), 0,5,"", null);

        assertEquals("{\"size\":5,\"query\":{\"match\":{\"abc\":{\"query\":\"test 123\"}}}}", s);
    }



    @Test
    public void testMatchWithSort() {


        LinkedHashMap<String, String> sort =new LinkedHashMap<>();
        sort.put("age", "desc");


        String s = EsQueryBuilder.build(Query.match(null, null)
                .setFieldQuery("abc","test 123"), 0,5,"", sort);

        assertEquals("{\"size\":5,\"query\":{\"match\":{\"abc\":{\"query\":\"test 123\"}}},\"sort\":{\"age\":\"desc\"}}", s);
    }

}
