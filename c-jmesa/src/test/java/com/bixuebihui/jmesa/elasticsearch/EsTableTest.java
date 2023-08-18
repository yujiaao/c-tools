package com.bixuebihui.jmesa.elasticsearch;

import com.bixuebihui.elasticsearch.ElasticSearchFilter;
import com.bixuebihui.elasticsearch.query.Query;
import com.bixuebihui.jmesa.entry.Filter;
import com.bixuebihui.jmesa.entry.Sort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.jmesa.limit.FilterSet;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static org.junit.jupiter.api.Assertions.assertEquals;

//@RunWith(MockitoJUnitRunner.class)
public class EsTableTest {
    public static String host = "http://localhost:9200";
    public static String username = "elastic";
    public static String password = "changeme";

    //convert to es query
    Query convert(com.bixuebihui.jmesa.entry.Query q) throws ParseException, JsonProcessingException {
        ElasticSearchFilter filter = EsTable.queryToFilter(q, "tableCaption", null);
        System.out.println(new ObjectMapper().writeValueAsString(filter));
        return filter.toQuery();
    }

    com.bixuebihui.jmesa.entry.Query filterSetToQuery(FilterSet filterSet) {
        com.bixuebihui.jmesa.entry.Query query = new com.bixuebihui.jmesa.entry.Query();
        query.setFilterSet(filterSet);
        return query;
    }

    FilterSet jsonToFilterSet(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, FilterSet.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    String end2end(String json) throws JsonProcessingException, ParseException {
        FilterSet filterSet = jsonToFilterSet(json);
        com.bixuebihui.jmesa.entry.Query query = filterSetToQuery(filterSet);
        Query q = convert(query);
        return (new ObjectMapper().writeValueAsString(q.toArray()));
    }

    @ParameterizedTest
    @MethodSource("addFixture")
    void testEnd2end(String source, String result) throws JsonProcessingException, ParseException {
        String res = end2end(source);
        Assertions.assertEquals(result, res);
    }

    private static Stream<Arguments> addFixture() {
        return Stream.of(
                // filters
                Arguments.of("{\"operator\":\"AND\",\"filterSets\":[],\"filters\":[{\"property\":\"sight_show_pv\",\"comparison\":\"IS\",\"value\":[\"300\"]}]}",
                        "{\"bool\":{\"must\":[{\"term\":{\"sight_show_pv\":{\"boost\":1.0,\"value\":\"300\"}}}]}}"),

                Arguments.of("{\"operator\":\"AND\",\"filterSets\":[],\"filters\":[{\"property\":\"sight_show_pv\",\"comparison\":\"IS\",\"value\":[\"300\"]}" +
                                ",{\"property\":\"req_pv\",\"value\":[\"500\"],\"comparison\":\"IS\"}]}",
                        "{\"bool\":{\"must\":[{\"term\":{\"sight_show_pv\":{\"boost\":1.0,\"value\":\"300\"}}},{\"term\":{\"req_pv\":{\"boost\":1.0,\"value\":\"500\"}}}]}}"),

                Arguments.of("{\"operator\":\"OR\",\"filterSets\":[],\"filters\":[{\"property\":\"sight_show_pv\",\"comparison\":\"IS\",\"value\":[\"300\"]}" +
                                ",{\"property\":\"req_pv\",\"value\":[\"500\"],\"comparison\":\"IS\"}]}",
                        "{\"bool\":{\"should\":[{\"term\":{\"sight_show_pv\":{\"boost\":1.0,\"value\":\"300\"}}},{\"term\":{\"req_pv\":{\"boost\":1.0,\"value\":\"500\"}}}]}}"),

                Arguments.of("{\"operator\":\"NOT\",\"filterSets\":[],\"filters\":[{\"property\":\"sight_show_pv\",\"comparison\":\"IS\",\"value\":[\"300\"]}" +
                                ",{\"property\":\"req_pv\",\"value\":[\"500\"],\"comparison\":\"IS\"}]}",
                        "{\"bool\":{\"must_not\":[{\"term\":{\"sight_show_pv\":{\"boost\":1.0,\"value\":\"300\"}}},{\"term\":{\"req_pv\":{\"boost\":1.0,\"value\":\"500\"}}}]}}"),

                //filterSets AND + OR
                Arguments.of("{\"operator\":\"AND\",\"filterSets\":[{\"operator\":\"AND\",\"filters\":[{\"property\":\"ctr\",\"comparison\":\"IS\",\"value\":[\"0.1\"]}]}," +
                                "{\"operator\":\"OR\",\"filters\":[" +
                                "{\"property\":\"sight_show_pv\",\"comparison\":\"IS\",\"value\":[\"300\"]}," +
                                "{\"property\":\"sight_show_pv\",\"comparison\":\"IS\",\"value\":[\"400\"]}]" +
                                "}]}",
                        "{\"bool\":{\"must\":[{\"bool\":{\"should\":[{\"term\":{\"sight_show_pv\":{\"boost\":1.0,\"value\":\"300\"}}},{\"term\":{\"sight_show_pv\":{\"boost\":1.0,\"value\":\"400\"}}}],\"must\":[{\"term\":{\"ctr\":{\"boost\":1.0,\"value\":\"0.1\"}}}]}}]}}"),

                //filterSets NOT + OR
                Arguments.of("{\"operator\":\"AND\",\"filterSets\":[{\"operator\":\"NOT\",\"filters\":[{\"property\":\"ctr\",\"comparison\":\"IS\",\"value\":[\"0.1\"]}]}," +
                                "{\"operator\":\"OR\",\"filters\":[" +
                                "{\"property\":\"sight_show_pv\",\"comparison\":\"IS\",\"value\":[\"300\"]}," +
                                "{\"property\":\"sight_show_pv\",\"comparison\":\"IS\",\"value\":[\"400\"]}]" +
                                "}]}",
                        "{\"bool\":{\"must\":[{\"bool\":{\"must_not\":[{\"term\":{\"ctr\":{\"boost\":1.0,\"value\":\"0.1\"}}}]," +
                                "\"should\":[{\"term\":{\"sight_show_pv\":{\"boost\":1.0,\"value\":\"300\"}}},{\"term\":{\"sight_show_pv\":{\"boost\":1.0,\"value\":\"400\"}}}]}}]}}"),

                //filterSets NOT + OR(IS, IS_NOT)
                Arguments.of("{\"operator\":\"AND\",\"filterSets\":[{\"operator\":\"NOT\",\"filters\":[{\"property\":\"ctr\",\"comparison\":\"IS\",\"value\":[\"0.1\"]}]}," +
                                "{\"operator\":\"OR\",\"filters\":[" +
                                "{\"property\":\"sight_show_pv\",\"comparison\":\"IS\",\"value\":[\"300\"]}," +
                                "{\"property\":\"num\",\"comparison\":\"IS_NOT\",\"value\":[\"400\"]}]" +
                                "}]}",
                        "{\"bool\":{\"must\":[{\"bool\":{\"must_not\":[{\"term\":{\"ctr\":{\"boost\":1.0,\"value\":\"0.1\"}}}]," +
                                "\"should\":[{\"term\":{\"sight_show_pv\":{\"boost\":1.0,\"value\":\"300\"}}}," +
                                "{\"bool\":{\"must_not\":[{\"term\":{\"num\":{\"boost\":1.0,\"value\":\"400\"}}}]}}]}}]}}"),

                //empty
                Arguments.of("{}",
                        "{\"match_all\":{}}")

        );
    }

    @Test
    public void json() throws SQLException, JsonProcessingException {
        String tableName = "test";
        EsTable et = new EsTable(host,
                username, password,
                tableName, tableName);

        // et.request = Mockito.mock(EsRequest);

        int size = 2;
        String res = et.json("{\"exportType\":\"json\", \"maxRows\":" + size + "}");
        ObjectMapper mapper = new ObjectMapper();
        Map map = mapper.readValue(res, Map.class);

        Object obj = map.get("data");
        assertEquals(size, ((List) obj).size());

        res = et.json("{\"exportType\":\"json\", \"maxRows\":1, \"page\":2}");
        Map map2 = mapper.readValue(res, Map.class);

        Object obj2 = map2.get("data");
        assertEquals(1, ((List) obj2).size());
    }

    @Test
    public void jsonWithSort() throws SQLException, JsonProcessingException {
        String tableName = "test";
        EsTable et = new EsTable(host,
                username, password,
                tableName, tableName);

        int size = 2;
        String res = et.json("{\"exportType\":\"json\", \"maxRows\":" + size + ", \"sort\":{\"height\":\"desc\",\"age\":\"desc\"}}");
        ObjectMapper mapper = new ObjectMapper();
        Map map = mapper.readValue(res, Map.class);

        Object obj = map.get("data");
        assertEquals(size, ((List) obj).size());
    }

    @Test
    void testComplexQuery() throws JsonProcessingException, ParseException {
        String bizType = "1,2";

        DateTime d = new DateTime();
        String todayDt = "2023-08-01";
        Long pageNo = 1L;

        com.bixuebihui.jmesa.entry.Query queryCmd = new com.bixuebihui.jmesa.entry.Query();

        List<Sort> sortList = new ArrayList<>();
        List<com.bixuebihui.jmesa.entry.Filter> filterList = new ArrayList<>();
        if (StringUtils.isNotEmpty(bizType)) {
            String[] bizTypes = bizType.split(",");
            List<Object> bizTypeList = Arrays.stream(bizTypes).collect(Collectors.toList());
            Filter filter3 = new Filter();
            filter3.setKey("biz_type");
            filter3.setValue(bizTypeList);
            filter3.setComparison("IN");
            filterList.add(filter3);
        }

        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

        int dataRangeType = 1;
        if (dataRangeType == 1) {
            //hour
            List<Object> hourList = new ArrayList<>();
            Integer lastestHours = 5;

            int hour = new DateTime().hourOfDay().get();
            if (hour < 10) {
                d = DateTime.parse(todayDt + " " + "0" + hour + ":00:00", format);
            } else {
                d = DateTime.parse(todayDt + " " + hour + ":00:00", format);
            }
            DateTime d1 = d.minusHours(1).toDateTime();
            DateTime d2 = d.minusHours(lastestHours).toDateTime();
            String endTime = d1.toString("yyyy-MM-dd HH:mm:ss");
            String beginTime = d2.toString("yyyy-MM-dd HH:mm:ss");
            hourList.add(beginTime);
            hourList.add(endTime);
            Filter filter4 = new Filter();
            filter4.setKey("format_time");
            filter4.setValue(hourList);
            filter4.setComparison("BETWEEN");
            filterList.add(filter4);
            Filter filter6 = new Filter();
            filter6.setKey("data_type");
            filter6.setValue(Arrays.asList(1));
            filter6.setComparison("IS");
            filterList.add(filter6);
        } else {
            //day
            List<Object> dtList = new ArrayList<>();
            Integer lastestDays = 6;
            for (int i = 1; i <= lastestDays; i++) {
                String lastestDt = d.minusDays(i).toString("yyyy-MM-dd");
                dtList.add(lastestDt);
            }
            Filter filter = new Filter();
            filter.setKey("dt");
            filter.setValue(dtList);
            filter.setComparison("IN");
            filterList.add(filter);
            Filter filter6 = new Filter();
            filter6.setKey("data_type");
            filter6.setValue(Arrays.asList(0));
            filter6.setComparison("IS");
            filterList.add(filter6);
        }
        String json = "{\"operator\":\"AND\",\"filterSets\":[{\"operator\":\"AND\",\"filterSets\":[],\"filters\":[{\"property\":\"ctr\",\"comparison\":\"GTE\",\"value\":[\"0.9\"]},{\"property\":\"ctr\",\"comparison\":\"LTE\",\"value\":[\"0.9\"]}]}],\"filters\":[]}";

        if (StringUtils.isNotEmpty(json)) {
            FilterSet filterSet = new ObjectMapper().readValue(json, FilterSet.class);
            queryCmd.setFilterSet(filterSet);
        }
        queryCmd.setPage(pageNo);
        queryCmd.setMaxRows(10000L);
        queryCmd.setExportType("json");

        int takeNumberType = 1;
        if (takeNumberType == 1) {
            //所有
            Sort s = new Sort();
            s.setField("sight_show_pv");
            s.setOrder("desc");
            sortList.add(s);
        } else {
            String[] config = "ctr,desc,10000".split(",");
            Sort s = new Sort();
            s.setField(config[0]);
            s.setOrder(config[1]);
            queryCmd.setMaxRows(10000L);
            sortList.add(s);
        }
        queryCmd.setSort(sortList);
        queryCmd.setFilter(filterList);


//        System.out.println(new ObjectMapper().writeValueAsString(queryCmd));

        Query q = convert(queryCmd);
        System.out.println(new ObjectMapper().writeValueAsString(q));
        String res = new ElasticSearchFilter().toEsObject(q, 0, 10, emptySet(), emptySet(), null, null, emptyMap());

        System.out.println(res);
    }



}
