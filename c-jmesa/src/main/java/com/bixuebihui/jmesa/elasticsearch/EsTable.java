package com.bixuebihui.jmesa.elasticsearch;


import com.bixuebihui.elasticsearch.ElasticSearchFilter;
import com.bixuebihui.elasticsearch.EsRequest;
import com.bixuebihui.jmesa.EasyTable;
import com.bixuebihui.jmesa.entry.Query;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jmesa.limit.Limit;
import org.jmesa.limit.LimitFactory;
import org.jmesa.limit.RowSelect;
import org.jmesa.limit.SortSet;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;

/**
 * @author xwx
 */
public class EsTable extends EasyTable {
    protected EsRequest request;
    public EsTable(String  host,String username, String password,String tableCaption, String indexName) {
        super();
        request =  new EsRequest(host, username, password, indexName);
        this.id = tableCaption;
        this.tableCaption = tableCaption;
    }

    @Override
    public String json( String jsonQuery) throws SQLException {
        try {
            Limit limit = getLimit(tableCaption, jsonQuery);
            return getSearchResult(limit);
        }catch (Exception e){
            throw new SQLException(e);
        }
    }

    private String getSearchResult(Limit limit) throws JsonProcessingException, ParseException {
        ElasticSearchFilter filter = new ElasticSearchFilter();
        getFilter(filter, limit, id);
        RowSelect rowSelect = limit.getRowSelect();
        LinkedHashMap<String,String> sort = toEsSort(limit.getSortSet());

        String scriptSource = null;
        Map<String, Object> scriptParams = emptyMap();

        String query = filter.toEsObject(filter.toQuery(), rowSelect.getRowStart(),
                rowSelect.getRowEnd() - rowSelect.getRowStart(), emptySet(),emptySet(), sort, scriptSource, scriptParams, null);
        return request.esJsonToTableJson(request.search(query, rowSelect.getTotalRows() ), rowSelect.getMaxRows(), rowSelect.getRowStart(), rowSelect.getRowEnd(), rowSelect.getTotalRows(), rowSelect.getPage());
    }

    /**
     * 测试用方法
     * @param query
     * @return
     * @throws ParseException
     */
    public static ElasticSearchFilter  queryToFilter(Query query, String tableCaption, String id) throws ParseException, JsonProcessingException {
        ElasticSearchFilter filter = new ElasticSearchFilter();
        Limit limit = getLimit(tableCaption, query);
//        System.out.println(new ObjectMapper().writeValueAsString(limit));

        getFilter(filter, limit, id);

        return filter;
    }

    @Override
    public String json( Query jsonQuery) throws SQLException {
        try {
            Limit limit = getLimit(tableCaption, jsonQuery);
            return getSearchResult(limit);
        }catch (Exception e){
            throw new SQLException(e);
        }
    }



    private static LinkedHashMap<String,String> toEsSort(SortSet sortSet) {
        LinkedHashMap<String,String> res = new LinkedHashMap<>();
        if(sortSet.isSorted()) {
            for (org.jmesa.limit.Sort sort :
                    sortSet.getSorts().stream().sorted(Comparator.comparingInt(org.jmesa.limit.Sort::getPosition)).collect(Collectors.toList())) {
                res.put(sort.getProperty(), sort.getOrder().toParam());
            }
        }
        return res;
    }


    public static Limit getLimit(String id, String json) {
        return new LimitFactory(id, json).createLimit();
    }

    public static Limit getLimit(String id, Query query) {
        return  new LimitFactory(id, query).createLimit();
    }

}
