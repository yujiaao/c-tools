package com.bixuebihui.elasticsearch;

import com.bixuebihui.elasticsearch.aggregation.AbstractAggregation;
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
     * { "term" : { "is_delete" : 0 } },
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
        if (isNoFilters(this)) {
            return Query.match_all();
        }

        return recursiveBool(this, null);
    }

    public Bool recursiveBool(SqlFilter sqlFilterUp, Bool criteria) {

        List<Bool> subCriteriaList = new ArrayList<>();
        if (sqlFilterUp.getSubGroups() != null) {
            for (SqlFilter sqlFilter : sqlFilterUp.getSubGroups()) {
                Bool subCriteria = new Bool();
                recursiveBool(sqlFilter, subCriteria);
                subCriteriaList.add(subCriteria);
            }
        }

        if (criteria == null) {
            // 为减少一层bool，只有一个子组时，不再创建bool
            if (subCriteriaList.size() == 1) {
                criteria = subCriteriaList.get(0);
                subCriteriaList = Collections.emptyList();
            } else {
                criteria = new Bool();
            }
        }

        buildEsObject(criteria, sqlFilterUp.getFilters(), sqlFilterUp.getJoinOperator(), subCriteriaList);

        return criteria;
    }

    public String toEsObject(Query query, int from, int size,
            Set<String> includeFields,
            Set<String> excludeFields,
            LinkedHashMap<String, String> sort, String scriptSource, Map<String, Object> scriptParams,
            AbstractAggregation aggs) {
        return EsQueryBuilder.build(query, from, size, includeFields, excludeFields, sort,
                scriptSource, scriptParams, aggs, Collections.emptyList());
    }

    public String toEsObject(Query query, int from, int size,
            Set<String> includeFields,
            Set<String> excludeFields,
            LinkedHashMap<String, String> sort, String scriptSource, Map<String, Object> scriptParams,
            AbstractAggregation aggs, List<Object> searchAfter) {
        return EsQueryBuilder.build(query, from, size, includeFields, excludeFields,
                sort, scriptSource, scriptParams, aggs, searchAfter);
    }

    private void addFilter(Bool criteria, Query filter, Operator upOperator) {

        switch (upOperator) {
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

    private void addFilter(Bool criteria, List<Query> filters, Operator upOperator) {
        if (filters.isEmpty()) return;
        switch (upOperator) {
            case OR:
                criteria.addShould(filters).setMinimumShouldMatch(1);
                break;
            case NOT:
                criteria.addMustNot(filters);
                break;
            default:
                criteria.addMust(filters);
                break;
        }
    }

    private void buildEsObject(Bool criteria, List<Filter> filters, Operator upOperator, List<Bool> addOnFilters) {

        List<Query> queryFilters = new ArrayList<>(upOperator != Operator.NOT ? addOnFilters : Collections.emptyList());
        List<Query> queryNotFilters = new ArrayList<>(
                upOperator == Operator.NOT ? addOnFilters : Collections.emptyList());

        if (filters != null) {
            filters.forEach(filter -> {
                        Query queryFilter;
                        switch (filter.getComparison()) {
                            case IS: {
                                queryFilter = Query.term(filter.getProperty(), filter.getValue()[0]);
                                queryFilters.add(queryFilter);
                            }
                            break;
                            case IS_NOT: {
                                queryFilter = Query.term(filter.getProperty(), filter.getValue()[0]);
                                //criteria.addMustNot(queryFilter);
                                queryNotFilters.add(queryFilter);
                            }
                            break;
                            case BETWEEN: {
                                List list = objToList(filter.getValue());
                                queryFilter = Query.range(null, null).between(filter.getProperty(), list.get(0), list.get(1));
                                queryFilters.add(queryFilter);
                            }
                            break;

                            case NOT_BETWEEN: {
                                List list1 = objToList(filter.getValue());
                                queryFilter = Query.range(null, null).between(filter.getProperty(), list1.get(0), list1.get(1));
                                //criteria.addMustNot(queryFilter);
                                queryNotFilters.add(queryFilter);
                            }
                            break;
                            case IS_NULL: {
                                queryFilter = Query.term(filter.getProperty(), null);
                                queryFilters.add(queryFilter);
                            }
                            break;
                            case IS_NOT_NULL: {
                                queryFilter = Query.term(filter.getProperty(), null);
                                //criteria.addMustNot(queryFilter);
                                queryNotFilters.add(queryFilter);
                            }
                            break;
                            case GT: {
                                queryFilter = Query.range(filter.getProperty(), ImmutableMap.builder().put(Range.GT, objToList(filter.getValue()).get(0)).build());
                                queryFilters.add(queryFilter);
                            }
                            break;
                            case GTE: {
                                queryFilter = Query.range(filter.getProperty(), ImmutableMap.builder().put(Range.GTE, objToList(filter.getValue()).get(0)).build());
                                queryFilters.add(queryFilter);
                            }
                            break;
                            case LT: {
                                queryFilter = Query.range(filter.getProperty(), ImmutableMap.builder().put(Range.LT, objToList(filter.getValue()).get(0)).build());
                                queryFilters.add(queryFilter);
                            }
                            break;
                            case LTE: {
                                queryFilter = Query.range(filter.getProperty(), ImmutableMap.builder().put(Range.LTE, objToList(filter.getValue()).get(0)).build());
                                queryFilters.add(queryFilter);
                            }
                            break;
                            case NOT_IN: {
                                queryFilter = Query.terms(filter.getProperty(), Lists.newArrayList(filter.getValue()));
                                //criteria.addMustNot(queryFilter);
                                queryNotFilters.add(queryFilter);
                            }
                            break;
                            case IN: {
                                queryFilter = Query.terms(filter.getProperty(), Lists.newArrayList(filter.getValue()));
                                queryFilters.add(queryFilter);
                            }
                            break;
                            case NOT_EXISTS: {
                                queryFilter = Query.exists(filter.getProperty());
                                //criteria.addMustNot(queryFilter);
                                queryNotFilters.add(queryFilter);
                            }
                            break;
                            case EXISTS: {
                                queryFilter = Query.exists(filter.getProperty());
                                queryFilters.add(queryFilter);
                            }
                            break;
                            case CONTAIN: {
                                queryFilter = Query.match(filter.getProperty(), filter.getValue()[0].toString());
                                queryFilters.add(queryFilter);
                            }
                            break;
                            case WILDCARD:
                                Arrays.asList(filter.getValue()).forEach(value ->
                                        criteria.addShould(Query.wildcard(filter.getProperty(), value + "*", 1)));
                                break;
                            default: {
                            }

                        }
                    }
            );
        }

        if (!queryNotFilters.isEmpty()) {
            Bool notCriteria = new Bool();
            notCriteria.addMustNot(queryNotFilters);
            queryFilters.add(notCriteria);
        }
        addFilter(criteria, queryFilters, upOperator);
    }



    private Query getMoreLikeThis(Filter filter) {
        Query queryFilter;
        List<String> docContent = new ArrayList<>();

        String[] fields = filter.getProperty().contains(",") ? filter.getProperty().split(",")
                : new String[] { filter.getProperty() };


        if (fields.length != filter.getValue().length) {
            throw new IllegalArgumentException("fields length is not equal to value length");
        }


        List<String> list = new ArrayList<>();
        for (Object value : filter.getValue()) {
            list.add(value.toString());
        }

        int minTermFreq = 1;
        int minDocFreq = 1;
        int maxQueryTerms = 50;
        double boostTerms = 1.5;
        String minimumShouldMatch = "90%";

        if (filter.getOtherParams() != null) {
            if (filter.getOtherParams().containsKey("min_term_freq")) {
                minTermFreq = Integer.parseInt(filter.getOtherParams().get("min_term_freq").toString());
            }
            if (filter.getOtherParams().containsKey("min_doc_freq")) {
                minDocFreq = Integer.parseInt(filter.getOtherParams().get("min_doc_freq").toString());
            }
            if (filter.getOtherParams().containsKey("max_query_terms")) {
                maxQueryTerms = Integer.parseInt(filter.getOtherParams().get("max_query_terms").toString());
            }
            if (filter.getOtherParams().containsKey("boost_terms")) {
                boostTerms = Double.parseDouble(filter.getOtherParams().get("boost_terms").toString());
            }
            if (filter.getOtherParams().containsKey("minimum_should_match")) {
                minimumShouldMatch = filter.getOtherParams().get("minimum_should_match").toString();
            }
        }
        queryFilter = Query.more_like_this()
                .setFields(Arrays.asList(fields))
                .setDoc(list)
                .setMinTermFreq(minTermFreq)
                .setMinDocFreq(minDocFreq)
                .setMaxQueryTerms(maxQueryTerms)
                .setBoostTerms(boostTerms)
                .setMinimumShouldMatch(minimumShouldMatch);
        return queryFilter;
    }

    private void buildEsObject(Bool criteria, Filter filter, Operator upOperator) {
        Query queryFilter;

        if (wantIgnoreNull(filter)) {
            return;
        }

        switch (filter.getComparison()) {
            case IS: {
                queryFilter = Query.term(filter.getProperty(), filter.getValue()[0]);
                addFilter(criteria, queryFilter, upOperator);
            }
            break;
            case IS_NOT: {
                queryFilter = Query.term(filter.getProperty(), filter.getValue()[0]);
                criteria.addMustNot(queryFilter);
            }
            break;
            case BETWEEN: {
                List list = objToList(filter.getValue());
                queryFilter = Query.range(null, null).between(filter.getProperty(), list.get(0), list.get(1));
                addFilter(criteria, queryFilter, upOperator);
            }
            break;
            case NOT_BETWEEN: {
                List list1 = objToList(filter.getValue());
                queryFilter = Query.range(null, null).between(filter.getProperty(), list1.get(0), list1.get(1));
                criteria.addMustNot(queryFilter);
            }
            break;
            case IS_NULL: {
                queryFilter = Query.term(filter.getProperty(), null);
                addFilter(criteria, queryFilter, upOperator);
            }
            break;
            case IS_NOT_NULL: {
                queryFilter = Query.term(filter.getProperty(), null);
                criteria.addMustNot(queryFilter);
            }
            break;
            case GT: {
                queryFilter = Query.range(filter.getProperty(), ImmutableMap.builder().put(Range.GT, objToList(filter.getValue()).get(0)).build());
                addFilter(criteria, queryFilter, upOperator);
            }
            break;
            case GTE: {
                queryFilter = Query.range(filter.getProperty(), ImmutableMap.builder().put(Range.GTE, objToList(filter.getValue()).get(0)).build());
                addFilter(criteria, queryFilter, upOperator);
            }
            break;
            case LT: {
                queryFilter = Query.range(filter.getProperty(), ImmutableMap.builder().put(Range.LT, objToList(filter.getValue()).get(0)).build());
                addFilter(criteria, queryFilter, upOperator);
            }
            break;
            case LTE: {
                queryFilter = Query.range(filter.getProperty(), ImmutableMap.builder().put(Range.LTE, objToList(filter.getValue()).get(0)).build());
                addFilter(criteria, queryFilter, upOperator);
            }
            break;
            case NOT_IN: {
                queryFilter = Query.terms(filter.getProperty(), Lists.newArrayList(filter.getValue()));
                criteria.addMustNot(queryFilter);
            }
            break;
            case IN: {
                queryFilter = Query.terms(filter.getProperty(), Lists.newArrayList(filter.getValue()));
                addFilter(criteria, queryFilter, upOperator);
            }
            break;
            case NOT_EXISTS: {
                queryFilter = Query.exists(filter.getProperty());
                criteria.addMustNot(queryFilter);
            }
            break;
            case EXISTS: {
                queryFilter = Query.exists(filter.getProperty());
                addFilter(criteria, queryFilter, upOperator);
            }
            break;
            case CONTAIN: {
                queryFilter = Query.match(filter.getProperty(), filter.getValue()[0].toString());
                addFilter(criteria, queryFilter, upOperator);
            }
            break;
            case WILDCARD:
                Arrays.asList(filter.getValue()).forEach(value ->
                        criteria.addShould(Query.wildcard(filter.getProperty(), value + "*", 1)));
                break;
            case MORE_LIKE_THIS: {
                // 处理MORE_LIKE_THIS类型
                queryFilter = getMoreLikeThis(filter);
                addFilter(criteria, queryFilter, upOperator);
                break;
            }
            default: {
            }
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
