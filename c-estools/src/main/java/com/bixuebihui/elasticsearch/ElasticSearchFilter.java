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

        for (Filter filter : filters) {
            buildEsObject(criteria, filter);
        }

        for (SqlFilter sqlFilter : subGroups) {

            Bool subCriteria = new Bool();
            for (Filter filter : sqlFilter.getFilters()) {
                buildEsObject(criteria, filter);
            }

            if (subGroupsJoinOperator == Operator.AND) {
                criteria.addMust(subCriteria);
            } else {
                criteria.addShould(subCriteria);
            }
        }

        return criteria;
    }

    public String toEsObject(Query query, int from, int size,
                             Set<String> includeFields,
                             Set<String> excludeFields,
                             LinkedHashMap<String, String> sort, String scriptSource, Map<String,Object> scriptParams) {
            return EsQueryBuilder.build(query,from, size,includeFields,excludeFields,  sort, scriptSource, scriptParams);
    }

    private void buildEsObject(Bool criteria, Filter filter) {
        Query queryFilter;

        if (wantIgnoreNull(filter)) {
            return;
        }

        switch (filter.getComparison()) {
            case IS:
                queryFilter = Query.term(filter.getProperty(), filter.getValue()[0]);
                criteria.addMust(queryFilter);
                break;
            case IS_NOT:
                queryFilter = Query.term(filter.getProperty(), filter.getValue()[0]);
                criteria.addMustNot(queryFilter);
                break;
            case BETWEEN:
                List list = objToList(filter.getValue());
                queryFilter = Query.range(null, null).between(filter.getProperty(), list.get(0), list.get(1));
                criteria.addMust(queryFilter);
                break;
            case NOT_BETWEEN:
                List list1 = objToList(filter.getValue());
                queryFilter = Query.range(null, null).between(filter.getProperty(), list1.get(0), list1.get(1));
                criteria.addMustNot(queryFilter);
            break;
            case IS_NULL:
                queryFilter = Query.term(filter.getProperty(), null);
                criteria.addMust(queryFilter);
                break;
            case IS_NOT_NULL:
                queryFilter = Query.term(filter.getProperty(), null);
                criteria.addMustNot(queryFilter);
                break;
            case GT:
                queryFilter = Query.range(filter.getProperty(), ImmutableMap.builder().put(Range.GT, objToList(filter.getValue()).get(0)).build());
                criteria.addMust(queryFilter);
                break;
            case GTE:
                queryFilter = Query.range(filter.getProperty(), ImmutableMap.builder().put(Range.GTE, objToList(filter.getValue()).get(0)).build());
                criteria.addMust(queryFilter);
                break;
            case LT:
                queryFilter = Query.range(filter.getProperty(), ImmutableMap.builder().put(Range.LT, objToList(filter.getValue()).get(0)).build());
                criteria.addMust(queryFilter);
                break;
            case LTE:
                queryFilter = Query.range(filter.getProperty(), ImmutableMap.builder().put(Range.LTE, objToList(filter.getValue()).get(0)).build());
                criteria.addMust(queryFilter);
                break;
            case NOT_IN:
                queryFilter = Query.terms(filter.getProperty(), Lists.newArrayList(filter.getValue()));
                criteria.addMustNot(queryFilter);
                break;
            case IN:
                queryFilter = Query.terms(filter.getProperty(), Lists.newArrayList(filter.getValue()));
                criteria.addMust(queryFilter);
                break;
            case NOT_EXISTS:
                queryFilter = Query.exists(filter.getProperty());
                criteria.addMustNot(queryFilter);
                break;
            case EXISTS:
                queryFilter = Query.exists(filter.getProperty());
                criteria.addMust(queryFilter);
                break;
            case CONTAIN:
                queryFilter = Query.match(filter.getProperty(), filter.getValue()[0].toString());
                criteria.addMust(queryFilter);
                break;

            case WILDCARD:
                Arrays.asList(filter.getValue()).forEach(value -> criteria.addShould( Query.wildcard(filter.getProperty(), value + "*", 1)));

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
                if(!isUseNullAsCondition() && isNullValue(filter)) {
                    return true;
                }
        }
        return false;
    }

    private boolean isNullValue(Filter filter) {
        return filter.getValue() == null || filter.getValue().length > 0 && filter.getValue()[0] == null;
    }

}
