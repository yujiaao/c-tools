package com.bixuebihui.elasticsearch;

import com.bixuebihui.elasticsearch.query.Bool;
import com.bixuebihui.elasticsearch.query.Query;
import com.bixuebihui.elasticsearch.query.Range;
import com.bixuebihui.jdbc.SqlFilter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import java.util.*;

/**
 * @author xwx
 */
public class ElasticSearchFilter extends SqlFilter {


    /**
     * Test cases:
     * <p>
     * GET /content/_search
     * {
     * "query":{
     * "match_all":{
     * }
     * }
     * }
     * <p>
     * GET /content* /_search
     * {
     * "query":{
     * "term" : {
     * "biz_id.keyword" : 105982
     * }
     * }
     * }
     * <p>
     * GET /content* /_search
     * {
     * "query": {
     * "bool" : {
     * "must" : { "term" : { "biz_id" : 105982 } },
     * "should" : [
     * { "term" : { "author_name" : "alice" }},
     * { "term" : { "is_delete" :  0 } },
     * { "term" : { "is_publish" : 1 } }
     * ],
     * "minimum_should_match" : 1
     * }
     * <p>
     * }
     * }
     * <p>
     * <p>
     * GET logstash-* /_search
     * {
     * "size": 20,
     * "from":0,
     * "query": {
     * "match": {
     * "cs_uri_stem": "get"
     * }，
     * }
     * "sort": [
     * {"@timestamp": {"order": "asc", "format": "strict_date_optional_time_nanos"}}
     * ],
     * }
     * <p>
     * match vs term: term is an exact query, match is a fuzzy query
     */

    private List objToList(Object value) {
        if (value instanceof List) {
            return (List) value;
        }
        if (value instanceof Object[]) {
            return Arrays.asList((Object[]) value);
        }
        return List.of(value);
    }

    public Query toQuery() {
        if (isNoFilters()) {
            return Query.match_all();
        }

        Bool criteria = new Bool();
        recursiveBool(this, criteria, this.filters);
        return criteria;
    }

    public void recursiveBool(SqlFilter sqlFilterUp, Bool criteria, List<Filter> filters) {

        if (filters != null) {
            for (Filter filter : filters) {
                buildEsObject(criteria, filter, sqlFilterUp.getSubGroupsJoinOperator());
            }
        }
        if(sqlFilterUp.getSubGroups()==null || sqlFilterUp.getSubGroups().isEmpty()){
            return;
        }

        Bool subCriteria = new Bool();


        for (SqlFilter sqlFilter : sqlFilterUp.getSubGroups()) {
            if(sqlFilter.getFilters()==null || sqlFilter.getFilters().isEmpty()){
                continue;
            }
            if(sqlFilter.getFilters().size()>1  && sqlFilter.getSubGroupsJoinOperator() == Operator.NOT) {
                Bool subGroupCriteria = new Bool();
                recursiveBool(sqlFilter, subGroupCriteria, sqlFilter.getFilters());
//                if (sqlFilter.getSubGroupsJoinOperator() == Operator.AND) {
//                    subCriteria.addMust(subGroupCriteria);
//                } else if (sqlFilter.getSubGroupsJoinOperator() == Operator.NOT) {
                    subCriteria.addMustNot(subGroupCriteria);
//                } else {
//                    //or
//                    subCriteria.addShould(subGroupCriteria);
//                }
            }else {
                recursiveBool(sqlFilter, subCriteria, sqlFilter.getFilters());
            }
        }

        if (sqlFilterUp.getSubGroupsJoinOperator() == Operator.AND) {
            criteria.addMust(subCriteria);
        } else if (sqlFilterUp.getSubGroupsJoinOperator() == Operator.NOT) {
            criteria.addMustNot(subCriteria);
        } else {
            //or
            criteria.addShould(subCriteria);
        }

    }

    public String toEsObject(Query query, int from, int size,
                             Set<String> includeFields,
                             Set<String> excludeFields,
                             LinkedHashMap<String, String> sort, String scriptSource, Map<String, Object> scriptParams) {
        return EsQueryBuilder.build(query, from, size, includeFields, excludeFields, sort, scriptSource, scriptParams);
    }

    private void  addFilter(Bool criteria, Query filter, Operator upOperator){
       switch (upOperator){
           case AND:
               criteria.addMust(filter);
               break;
           case OR:
               criteria.addShould(filter);
               break;
           case NOT:
               criteria.addMustNot(filter);
               break;
           default:
               criteria.addMust(filter);
       }
    }
    private void buildEsObject(Bool criteria, Filter filter, Operator upOperator){
        Query queryFilter;

        if (wantIgnoreNull(filter)) {
            return;
        }

        switch (filter.getComparison()) {
            case IS:
                queryFilter = Query.term(filter.getProperty(), filter.getValue()[0]);
                addFilter( criteria,  queryFilter,  upOperator);
                break;
            case IS_NOT:
                queryFilter = Query.term(filter.getProperty(), filter.getValue()[0]);
                criteria.addMustNot(queryFilter);
                break;
            case BETWEEN:
                List list = objToList(filter.getValue());
                queryFilter = Query.range(null, null).between(filter.getProperty(), list.get(0), list.get(1));
                addFilter( criteria,  queryFilter,  upOperator);
                break;
            case NOT_BETWEEN:
                List list1 = objToList(filter.getValue());
                queryFilter = Query.range(null, null).between(filter.getProperty(), list1.get(0), list1.get(1));
                criteria.addMustNot(queryFilter);
                break;
            case IS_NULL:
                queryFilter = Query.term(filter.getProperty(), null);
                addFilter( criteria,  queryFilter,  upOperator);
                break;
            case IS_NOT_NULL:
                queryFilter = Query.term(filter.getProperty(), null);
                criteria.addMustNot(queryFilter);
                break;
            case GT:
                queryFilter = Query.range(filter.getProperty(), ImmutableMap.builder().put(Range.GT, objToList(filter.getValue()).get(0)).build());
                addFilter( criteria,  queryFilter,  upOperator);
                break;
            case GTE:
                queryFilter = Query.range(filter.getProperty(), ImmutableMap.builder().put(Range.GTE, objToList(filter.getValue()).get(0)).build());
                addFilter( criteria,  queryFilter,  upOperator);
                break;
            case LT:
                queryFilter = Query.range(filter.getProperty(), ImmutableMap.builder().put(Range.LT, objToList(filter.getValue()).get(0)).build());
                addFilter( criteria,  queryFilter,  upOperator);
                break;
            case LTE:
                queryFilter = Query.range(filter.getProperty(), ImmutableMap.builder().put(Range.LTE, objToList(filter.getValue()).get(0)).build());
                addFilter( criteria,  queryFilter,  upOperator);
                break;
            case NOT_IN:
                queryFilter = Query.terms(filter.getProperty(), Lists.newArrayList(filter.getValue()));
                criteria.addMustNot(queryFilter);
                break;
            case IN:
                queryFilter = Query.terms(filter.getProperty(), Lists.newArrayList(filter.getValue()));
                addFilter( criteria,  queryFilter,  upOperator);
                break;
            case NOT_EXISTS:
                queryFilter = Query.exists(filter.getProperty());
                criteria.addMustNot(queryFilter);
                break;
            case EXISTS:
                queryFilter = Query.exists(filter.getProperty());
                addFilter( criteria,  queryFilter,  upOperator);
                break;
            case CONTAIN:
                queryFilter = Query.match(filter.getProperty(), filter.getValue()[0].toString());
                addFilter( criteria,  queryFilter,  upOperator);
                break;

            case WILDCARD:
                Arrays.asList(filter.getValue()).forEach(value ->
                        criteria.addShould(Query.wildcard(filter.getProperty(), value + "*", 1)));

                break;
            default:
        }
    }

    private boolean wantIgnoreNull(Filter filter) {
        switch (filter.getComparison()) {
            case IS_NOT_NULL:
            case IS_NULL:
                break;
            default:
                if (!isUseNullAsCondition() && isNullValue(filter)) {
                    return true;
                }
        }
        return false;
    }

    private boolean isNullValue(Filter filter) {
        return filter.getValue() == null || filter.getValue().length > 0 && filter.getValue()[0] == null;
    }

}
