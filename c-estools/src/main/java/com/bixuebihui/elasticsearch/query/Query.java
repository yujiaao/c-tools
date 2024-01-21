package com.bixuebihui.elasticsearch.query;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * elasticsearch query DSL.
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-queries.html
 */
public class Query extends Params {

    static final String OPERATOR_OR = "or";
    static final String OPERATOR_AND = "and";

    /**
     * Sets maximum number of results for this query.
     *
     * @param size int $size Maximal number of results for query (default = 10)
     *
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-body.html#request-body-search-from-size">...</a>
     */
    public Query setSize(int size)
    {
         this.setParam("size", size);
         return this;
    }
    /**
     * Enables explain on the query.
     *
     * @param  $explain boolean Enabled or disable explain (default = true)
     *
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-body.html#request-body-search-explain">...</a>
     */
    public Query setExplain(boolean $explain)
    {
         this.setParam("explain", $explain);
        return this;
    }

    /**
     * Adds a track_total_hits argument.
     *
     * @param $trackTotalHits bool|int $trackTotalHits Track total hits parameter
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-body.html#request-body-search-track-total-hits">...</a>
     */
    public Query setTrackTotalHits(boolean $trackTotalHits)
    {
        this.setParam("track_total_hits", $trackTotalHits);
        return this;
    }

    /**
     * match query.
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-match-query.html
     *
     * @param field  field
     * @param values values
     * @return Match
     */
    public static Match match(String field, String values) {
        return new Match(field, values);
    }

    /**
     * match_phrase query.
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-match-phrase-query.html
     *
     * @param field  field
     * @param values values
     * @return MatchPhrase
     */
    public static MatchPhrase match_phrase(String field, String values) {
        return new MatchPhrase(field, values);
    }


    /**
     * multi match query.
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-multi-match-query.html
     *
     * @return \Elastica\Query\MultiMatch
     */
    public static MultiMatch multi_match() {
        return new MultiMatch();
    }

    /**
     * bool query.
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-bool-query.html
     *
     * @return \Elastica\Query\BoolQuery
     */
    public static Bool bool() {
        return new Bool();
    }

    /**
     * boosting query.
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-boosting-query.html
     *
     * @return Boosting
     */
    public static Boosting boosting() {
        return new Boosting();
    }

    /**
     * common terms query.
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-common-terms-query.html
     *
     * @param field           string
     * @param query           string
     * @param cutoffFrequency percentage in decimal form (.001 == 0.1%)
     * @return Common
     */
    public static Common common_terms(String field, String query, float cutoffFrequency) {
        return new Common(field, query, cutoffFrequency);
    }

    /**
     * ids query.
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-ids-query.html
     *
     * @param ids map
     * @return Ids
     */
    public static Ids ids(List ids) {
        return new Ids(ids);
    }

    /**
     * match all query.
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-match-all-query.html
     *
     * @return MatchAll
     */
    public static MatchAll match_all() {
        return new MatchAll();
    }

    /**
     * match none query.
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-match-all-query.html#query-dsl-match-none-query
     *
     * @return MatchNone
     */
    public static MatchNone match_none() {
        return new MatchNone();
    }

    /**
     * more like this query.
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-mlt-query.html
     *
     * @return MoreLikeThis
     */
    public static MoreLikeThis more_like_this() {
        return new MoreLikeThis();
    }

    /**
     * nested query.
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-nested-query.html
     *
     * @return Nested
     */
    public static Nested nested() {
        return new Nested();
    }

    /**
     * @param type
     * @param id
     * @param ignoreUnmapped default false
     * @return ParentId ParentId
     */
    public static ParentId parent_id(String type, String id, boolean ignoreUnmapped) {
        return new ParentId(type, id, ignoreUnmapped);
    }

    /**
     * prefix query.
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-prefix-query.html
     *
     * @param prefix Prefix array
     * @return Prefix
     */
    public static Prefix prefix(List prefix) {
        return new Prefix(prefix);
    }

    /**
     * query string query.
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-query-string-query.html
     *
     * @param queryString OPTIONAL Query string for object
     * @return QueryString
     */
    public static QueryString query_string(String queryString) {
        return new QueryString(queryString);
    }

    /**
     * simple_query_string query.
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-simple-query-string-query.html
     *
     * @param query  string
     * @param fields array
     * @return SimpleQueryString
     */
    public static SimpleQueryString simple_query_string(String query, List fields) {
        return new SimpleQueryString(query, fields);
    }

    /**
     * range query.
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-range-query.html
     *
     * @param fieldName string
     * @param args      array
     * @return Range
     */
    public static Range range(String fieldName, Map args) {
        return new Range(fieldName, args);
    }

    /**
     * regexp query.
     *
     * @param key   string
     * @param value string
     * @param boost float
     * @return Regexp
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-regexp-query.html
     */
    public static Regexp regexp(String key, String value, float boost) {
        return new Regexp(key, value, boost);
    }

    /**
     * span first query.
     *
     * @param \Elastica\Query\AbstractQuery|array match
     * @param end                                 int
     * @return SpanFirst
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-span-first-query.html
     */
    public static SpanFirst span_first(Query match, Integer end) {
        return new SpanFirst(match, end);
    }

    /**
     * span multi term query.
     *
     * @param \Elastica\Query\AbstractQuery|array match
     * @return SpanMulti
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-span-multi-term-query.html
     */
    public static SpanMulti span_multi_term(Query match) {
        return new SpanMulti(match);
    }

    /**
     * span near query.
     *
     * @param clauses array
     * @param slop    default 1
     * @param inOrder default false
     * @return SpanNear
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-span-near-query.html
     */
    public static SpanNear span_near(Map clauses, int slop, boolean inOrder) {
        return new SpanNear(clauses, slop, inOrder);
    }

    /**
     * span not query.
     *
     * @param include AbstractSpanQuery|null
     * @param exclude AbstractSpanQuery|null
     * @return SpanNot
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-span-not-query.html
     */
    public static SpanNot span_not(AbstractSpanQuery include, AbstractSpanQuery exclude) {
        return new SpanNot(include, exclude);
    }

    /**
     * span_or query.
     *
     * @param clauses array
     * @return SpanOr
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-span-or-query.html
     */
    public static SpanOr span_or(List clauses) {
        return new SpanOr(clauses);
    }

    /**
     * span_term query.
     *
     * @param term array
     * @return SpanTerm
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-span-term-query.html
     */
    public static SpanTerm span_term(List term) {
        return new SpanTerm(term);
    }

    /**
     * span_containing query.
     *
     * @param little AbstractSpanQuery|null
     * @param big    AbstractSpanQuery|null
     * @return SpanContaining
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-span-containing-query.html
     */
    public static SpanContaining span_containing(AbstractSpanQuery little, AbstractSpanQuery big) {
        return new SpanContaining(little, big);
    }

    /**
     * span_within query.
     *
     * @param little AbstractSpanQuery|null
     * @param big    AbstractSpanQuery|null
     * @return SpanWithin
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-span-within-query.html
     */
    public static SpanWithin span_within(AbstractSpanQuery little, AbstractSpanQuery big) {
        return new SpanWithin(little, big);
    }

    /**
     * term query.
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-term-query.html
     *
     * @param term array
     * @return Term
     */
    public static Term term(Map term) {
        return new Term(term);
    }

    public static Term term(String key, Object value) {
        return new Term(key, value);
    }

    /**
     * terms query.
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-terms-query.html
     *
     * @param key   string
     * @param terms array
     * @return Terms
     */
    public static Terms terms(String key, List terms) {
        return new Terms(key, terms);
    }

    /**
     * wildcard query.
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-wildcard-query.html
     *
     * @param key   OPTIONAL Wildcard key
     * @param value OPTIONAL Wildcard value
     * @param boost OPTIONAL Boost value (default = 1)
     * @return Wildcard
     */
    public static Wildcard wildcard(String key, String value, float boost) {
        return new Wildcard(key, value, boost);
    }

    /**
     * geo distance query.
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-geo-distance-query.html
     *
     * @param key      string
     * @param location array|string
     * @param distance string
     * @return GeoDistance
     */
    public static GeoDistance geo_distance(String key, List<String> location, String distance) {
        return new GeoDistance(key, location, distance);
    }

    /**
     * exists query.
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-exists-query.html
     * Returns documents that contain an indexed value for a field.
     * @param field
     * @return Exists
     */
    public static Exists exists(String field) {
        return new Exists(field);
    }

    /**
     * percolate query.
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/5.0/query-dsl-percolate-query.html
     *
     * @return Percolate
     */
    public static Percolate percolate() {
        return new Percolate();
    }

    /**
     * must return type for QueryBuilder usage.
     *
     * @return string
     */
    public DSL getType() {
        return DSL.TYPE_QUERY;
    }

    /**
     * constant score query.
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-constant-score-query.html
     *
     * @param filter Query|array|null
     * @return ConstantScore
     */
    public ConstantScore constant_score(Query filter) {
        return new ConstantScore(filter);
    }

    /**
     * dis max query.
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-dis-max-query.html
     *
     * @return DisMax
     */
    public DisMax dis_max() {
        return new DisMax();
    }

    /**
     * function score query.
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-function-score-query.html
     *
     * @return FunctionScore
     */
    public FunctionScore function_score() {
        return new FunctionScore();
    }

    /**
     * fuzzy query.
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-fuzzy-query.html
     *
     * @param fieldName Field name
     * @param value     String to search for
     * @return Fuzzy
     */
    public Fuzzy fuzzy(String fieldName, String value) {
        return new Fuzzy(fieldName, value);
    }

    /**
     * geo shape query.
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-geo-shape-query.html
     */
    public Query geo_shape() {
        throw new NotImplementedException();
    }

    /**
     * has child query.
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-has-child-query.html
     *
     * @param query string|\Elastica\Query|\Elastica\Query\AbstractQuery query
     * @param type  string                                               type  Parent document type
     *              query
     * @return HasChild
     */
    public HasChild has_child(String query, String type) {
        return new HasChild(query, type);
    }

    public HasChild has_child(Query query, String type) {
        return new HasChild(query, type);
    }

    /**
     * has parent query.
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-has-parent-query.html
     *
     * @param query string|Query
     * @param type  string                                               type  Parent document type
     * @return HasParent
     */
    public Query has_parent(String query, String type) {
        return new HasParent(query, type);
    }


}


/**
 * Constant score query.
 *
 * @author Nicolas Ruflin <spam@ruflin.com>
 * <p>
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-constant-score-query.html
 */
class ConstantScore extends Query {
    /**
     * Construct constant score query.
     *
     * @param filter Query|array|null
     */
    public ConstantScore(Query filter) {
        if (filter != null) {
            this.setFilter(filter);
        }
    }

    /**
     * Set filter.
     *
     * @param filter array|AbstractQuery
     * @return this
     */
    public ConstantScore setFilter(Query filter) {
        return (ConstantScore) this.setParam("filter", filter);
    }

    /**
     * Set boost.
     *
     * @param boost float
     * @return this
     */
    public ConstantScore setBoost(float boost) {
        return (ConstantScore) this.setParam("boost", boost);
    }
}

class FunctionScore extends Query {

}

class MoreLikeThis extends Query {

}

/**
 * Returns documents based on their IDs. This query uses document IDs stored in the _id field.
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-ids-query.html
 */
class Ids extends Query {
    /**
     * Creates filter object.
     *
     * @param ids List of ids
     */
    public Ids(List<String> ids) {
        this.setIds(ids);
    }

    /**
     * Adds one more filter to the and filter.
     *
     * @param id Adds id to filter
     * @return this
     */
    public Ids addId(String id) {
        ((List<String>) this.getParams().get("values")).add(id);

        return this;
    }

    /**
     * Sets the ids to filter.
     *
     * @param ids list|string  List of ids
     * @return this
     */
    public Ids setIds(Object ids) {
        if (ids instanceof List) {
            this.getParams().put("values", ids);
        } else {
            ((List) this.getParams().get("values")).add(ids);
        }

        return this;
    }

    /**
     * Converts filter to array.
     * <p>
     * Query::toArray()
     *
     * @return array Query
     */
    @Override
    public Map toArray() {
        return ImmutableMap.<String, Object>builder().put("ids", this.getParams()).build();
    }
}

class ParentId extends Query {

    public ParentId(String type, String id, boolean ignoreUnmapped) {
        super();
    }
}

class HasChild extends Query {

    public HasChild(String query, String type) {
        super();
    }

    public HasChild(Query query, String type) {
        super();
    }
}

class HasParent extends Query {

    public HasParent(String query, String type) {
        super();
    }
}

class DisMax extends Query {

}


/**
 * <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-match-all-query.html">match-all-query</a>
 */
class MatchAll extends Query {
}

class MatchNone extends Query {
}

class SimpleQueryString extends Query {

    public SimpleQueryString(String query, List fields) {
        super();
    }
}

/**
 * QueryString query.
 *
 * @author Nicolas Ruflin <spam@ruflin.com>, Jasper van Wanrooy <jasper@vanwanrooy.net>
 * <p>
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-query-string-query.html
 */
class QueryString extends Query {
    /**
     * Query string.
     *
     * @var Query string
     */
    protected String queryString;

    /**
     * Creates query string object. Calls setQuery with argument.
     *
     * @param queryString OPTIONAL Query string for object
     */
    public QueryString(String queryString) {
        this.setQuery(queryString);
    }

    /**
     * Sets a new query string for the object.
     *
     * @param query Query string
     * @return this
     * @throws \Elastica\Exception\InvalidException If given parameter is not a string
     */
    public QueryString setQuery(String query) {
        if (query == null) {
            throw new IllegalArgumentException("Parameter has to be a string");
        }

        return (QueryString) this.setParam("query", query);
    }

    /**
     * Sets the default field.
     * You cannot set fields and default_field.
     * <p>
     * If no field is set, _all is chosen
     *
     * @param field Field
     * @return this
     */
    public QueryString setDefaultField(String field) {
        return (QueryString) this.setParam("default_field", field);
    }

    /**
     * Sets the default operator AND or OR.
     * <p>
     * If no operator is set, OR is chosen
     *
     * @param operator Operator
     * @return this
     */
    public QueryString setDefaultOperator(String operator) {
        return (QueryString) this.setParam("default_operator", operator);
    }

    /**
     * Sets the analyzer to analyze the query with.
     *
     * @param analyzer Analyser to use
     * @return this
     */
    public QueryString setAnalyzer(String analyzer) {
        return (QueryString) this.setParam("analyzer", analyzer);
    }

    /**
     * Sets the parameter to allow * and ? as first characters.
     * <p>
     * If not set, defaults to true.
     *
     * @param allow bool
     * @return this
     */
    public QueryString setAllowLeadingWildcard(boolean allow) {
        return (QueryString) this.setParam("allow_leading_wildcard", allow);
    }

    /**
     * Sets the parameter to enable the position increments in result queries.
     * <p>
     * If not set, defaults to true.
     *
     * @param enabled defalut true
     * @return this
     */
    public QueryString setEnablePositionIncrements(boolean enabled) {
        return (QueryString) this.setParam("enable_position_increments", enabled);
    }

    /**
     * Sets the fuzzy prefix length parameter.
     * <p>
     * If not set, defaults to 0.
     *
     * @param length default 0
     * @return this
     */
    public QueryString setFuzzyPrefixLength(int length) {
        return (QueryString) this.setParam("fuzzy_prefix_length", (int) length);
    }

    /**
     * Sets the fuzzy minimal similarity parameter.
     * <p>
     * If not set, defaults to 0.5
     *
     * @param minSim
     * @return this
     */
    public QueryString setFuzzyMinSim(float minSim) {
        return (QueryString) this.setParam("fuzzy_min_sim", minSim);
    }

    /**
     * Sets the phrase slop.
     * <p>
     * If zero, exact phrases are required.
     * If not set, defaults to zero.
     *
     * @param phraseSlop
     * @return this
     */
    public QueryString setPhraseSlop(int phraseSlop) {
        return (QueryString) this.setParam("phrase_slop", phraseSlop);
    }

    /**
     * Sets the boost value of the query.
     * <p>
     * If not set, defaults to 1.0.
     *
     * @param boost
     * @return this
     */
    public QueryString setBoost(float boost) {
        return (QueryString) this.setParam("boost", boost);
    }

    /**
     * Allows analyzing of wildcard terms.
     * <p>
     * If not set, defaults to true
     *
     * @param analyze
     * @return this
     */
    public QueryString setAnalyzeWildcard(boolean analyze) {
        return (QueryString) this.setParam("analyze_wildcard", analyze);
    }

    /**
     * Sets the param to automatically generate phrase queries.
     * <p>
     * If not set, defaults to true.
     *
     * @param autoGenerate
     * @return this
     */
    public QueryString setAutoGeneratePhraseQueries(boolean autoGenerate) {
        return (QueryString) this.setParam("auto_generate_phrase_queries", autoGenerate);
    }

    /**
     * Sets the fields. If no fields are set, _all is chosen.
     * You cannot set fields and default_field.
     *
     * @param fields Fields
     * @return this
     * @throws \Elastica\Exception\InvalidException If given parameter is not an array
     */
    public QueryString setFields(List fields) {
        if (fields.isEmpty()) {
            throw new IllegalArgumentException("Parameter has to be an array");
        }

        return (QueryString) this.setParam("fields", fields);
    }

    /**
     * Whether to use bool or dis_max queries to internally combine results for multi field search.
     *
     * @param value Determines whether to use
     * @return this
     */
    public QueryString setUseDisMax(boolean value) {
        return (QueryString) this.setParam("use_dis_max", value);
    }

    /**
     * When using dis_max, the disjunction max tie breaker.
     * <p>
     * If not set, defaults to 0.
     *
     * @param tieBreaker
     * @return this
     */
    public QueryString setTieBreaker(float tieBreaker) {
        return (QueryString) this.setParam("tie_breaker", tieBreaker);
    }

    /**
     * Set a re-write condition. See https://github.com/elasticsearch/elasticsearch/issues/1186 for additional information.
     *
     * @param rewrite string
     * @return this
     */
    public QueryString setRewrite(String rewrite) {
        return (QueryString) this.setParam("rewrite", rewrite);
    }

    /**
     * Set timezone option.
     *
     * @param timezone string
     * @return this
     */
    public QueryString setTimezone(String timezone) {
        return (QueryString) this.setParam("time_zone", timezone);
    }

    /**
     * Converts query to array.
     *
     * @return array Query array
     * @see Query#toArray()
     */
    @Override
    public Map toArray() {
        Map res = Maps.newHashMap();
        Map res1 = Maps.newHashMap();

        res1.put("query", this.queryString);
        res1.putAll(this.getParams());
        res.put("query_string", res1);
        return res;

    }
}

class Nested extends Query {

}

class Prefix extends Query {

    public Prefix(List prefix) {
        super();
    }
}

class Fuzzy extends Query {
    /**
     * Construct a fuzzy query.
     *
     * @param fieldName Field name
     * @param value     String to search for
     */
    public Fuzzy(String fieldName, String value) {
        if (fieldName != null && value != null) {
            this.setField(fieldName, value);
        }
    }

    /**
     * Set field for fuzzy query.
     *
     * @param fieldName Field name
     * @param value     String to search for
     * @return this
     */
    public Fuzzy setField(String fieldName, String value) {
        if (value == null || fieldName == null) {
            throw new IllegalArgumentException("The field and value arguments must be of type string.");
        }
        if (this.getParams().size() > 0 && !this.getParams().keySet().contains(fieldName)) {
            throw new IllegalArgumentException("Fuzzy query can only support a single field.");
        }

        return (Fuzzy) this.setParam(fieldName, ImmutableMap.<String, String>builder().put("value", value).build());
    }

    /**
     * Set optional parameters on the existing query.
     *
     * @param option string  option name
     * @param value  mixed    Value of the parameter
     * @return this
     */
    public Fuzzy setFieldOption(String option, Object value) {
        //Retrieve the single existing field for alteration.
        Map params = this.getParams();
        if (params.size() < 1) {
            throw new IllegalArgumentException("No field has been set");
        }
        String key = (String) params.keySet().toArray()[0];
        ((Map) params.get(key)).put(option, value);

        return (Fuzzy) this.setParam(key, params.get(key));
    }
}

class Regexp extends Query {

    public Regexp(String key, String value, float boost) {
        super();
    }
}


/**
 * 跨度查询是低级位置查询，它提供对指定术语的顺序和接近度的专家控制。这些通常用于对法律文件或专利进行非常具体的查询。
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/span-queries.html
 */
class AbstractSpanQuery extends Query {

}

class SpanOr extends AbstractSpanQuery {

    public SpanOr(List clauses) {
        super();
    }
}

class SpanWithin extends AbstractSpanQuery {

    public SpanWithin(AbstractSpanQuery little, AbstractSpanQuery big) {
        super();
    }
}

class SpanNot extends AbstractSpanQuery {

    public SpanNot(AbstractSpanQuery include, AbstractSpanQuery exclude) {
        super();
    }
}

class SpanTerm extends AbstractSpanQuery {

    public SpanTerm(List term) {
        super();
    }
}

class SpanNear extends AbstractSpanQuery {

    public SpanNear(Map clauses, int slop, boolean inOrder) {
        super();
    }
}

class SpanContaining extends AbstractSpanQuery {

    public SpanContaining(AbstractSpanQuery little, AbstractSpanQuery big) {
        super();
    }
}

/**
 * * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-span-first-query.html
 */
class SpanFirst extends AbstractSpanQuery {

    public SpanFirst(Query match, Integer end) {
        super();
    }
}

class SpanMulti extends AbstractSpanQuery {

    public SpanMulti(Query match) {
        super();
    }
}


class ScoreFunction extends Query {

}


/**
 * Wildcard query.
 *
 * @author Nicolas Ruflin <spam@ruflin.com>
 * <p>
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-wildcard-query.html
 */
class Wildcard extends Query {


    /**
     * Construct wildcard query.
     *
     * @param key   OPTIONAL Wildcard key
     * @param value OPTIONAL Wildcard value
     * @param boost OPTIONAL Boost value (default = 1)
     */
    public Wildcard(String key, String value, float boost) {
        if (StringUtils.isNotEmpty(key)) {
            this.setValue(key, value, boost);
        }
    }

    /**
     * Sets the query expression for a key with its boost value.
     *
     * @param key   string
     * @param value string
     * @param boost float
     * @return $this
     */
    public Wildcard setValue(String key, String value, float boost) {

        Map res = Maps.newHashMap();
        res.put("value", value);
        res.put("boost", boost);

        return (Wildcard) (this.setParam(key, res));
    }
}

class Boosting extends Query {
    static final float NEGATIVE_BOOST = (float) 0.2;

    /**
     * Set the positive query for this Boosting Query.
     *
     * @param query Query
     * @return this
     */
    public Boosting setPositiveQuery(Query query) {
        return (Boosting) this.setParam("positive", query);
    }

    /**
     * Set the negative query for this Boosting Query.
     *
     * @param query
     * @return this
     */
    public Boosting setNegativeQuery(Query query) {
        return (Boosting) this.setParam("negative", query);
    }

    /**
     * Set the negative_boost parameter for this Boosting Query.
     *
     * @param negativeBoost float
     * @return this
     */
    public Boosting setNegativeBoost(float negativeBoost) {
        return (Boosting) this.setParam("negative_boost", negativeBoost);
    }
}


/**
 * * @author Oleg Cherniy <oleg.cherniy@gmail.com>
 * *
 * * @see https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-exists-query.html
 * <p>
 * An indexed value may not exist for a document’s field due to a variety of reasons:
 * <p>
 * The field in the source JSON is null or []
 * The field has "index" : false set in the mapping
 * The length of the field value exceeded an ignore_above setting in the mapping
 * The field value was malformed and ignore_malformed was defined in the mapping
 * <p>
 * GET /_search
 * {
 * "query": {
 * "exists": {
 * "field": "user"
 * }
 * }
 * }
 * Returns documents that contain an indexed value for a field.
 */
class Exists extends Query {

    public Exists(String field) {
        super();
        this.getParams().put("field", field);
    }
}

class GeoDistance extends Query {

    public GeoDistance(String key, List<String> location, String distance) {
        super();
    }
}


class Common extends Query {


    /**
     * @var string
     */
    protected String field;
    protected float cutoffFrequency;

    /**
     * @var array
     */
    protected Map<String, Object> queryParams = new HashMap<>();

    /**
     * @param field           the field on which to query
     * @param query           the query string
     * @param cutoffFrequency percentage in decimal form (.001 == 0.1%)
     */
    public Common(String field, String query, float cutoffFrequency) {
        this.setField(field);
        this.setQuery(query);
        this.cutoffFrequency = cutoffFrequency;
    }

    /**
     * Set the field on which to query.
     *
     * @param field the field on which to query
     * @return this
     */
    public Common setField(String field) {
        this.field = field;
        return this;
    }

    /**
     * Set the query string for this query.
     *
     * @param query
     * @return this
     */
    public Common setQuery(String query) {
        return this.setQueryParam("query", query);
    }

    /**
     * Set the frequency below which terms will be put in the low frequency group.
     *
     * @param frequency percentage in decimal form (.001 == 0.1%)
     * @return this
     */
    public Common setCutoffFrequency(float frequency) {
        return this.setQueryParam("cutoff_frequency", frequency);
    }

    /**
     * Set the logic operator for low frequency terms.
     *
     * @param operator see OPERATOR_* class constants for options
     * @return this
     */
    public Common setLowFrequencyOperator(String operator) {
        return this.setQueryParam("low_freq_operator", operator);
    }

    /**
     * Set the logic operator for high frequency terms.
     *
     * @param operator see OPERATOR_* class constants for options
     * @return this
     */
    public Common setHighFrequencyOperator(String operator) {
        return this.setQueryParam("high_frequency_operator", operator);
    }

    /**
     * Set the minimum_should_match parameter.
     *
     * @param minimum int|string  minimum number of low frequency terms which must be present
     * @return this
     * <p>
     * Possible values for minimum_should_match https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-minimum-should-match.html
     */
    public Common setMinimumShouldMatch(String minimum) {
        return this.setQueryParam("minimum_should_match", minimum);
    }

    public Common setMinimumShouldMatch(int minimum) {
        return this.setQueryParam("minimum_should_match", minimum);
    }

    /**
     * Set the boost for this query.
     *
     * @param boost
     * @return this
     */
    public Common setBoost(float boost) {
        return this.setQueryParam("boost", boost);
    }

    /**
     * Set the analyzer for this query.
     *
     * @param analyzer
     * @return this
     */
    public Common setAnalyzer(String analyzer) {
        return this.setQueryParam("analyzer", analyzer);
    }

    /**
     * Set a parameter in the body of this query.
     *
     * @param key   parameter key
     * @param value mixed  parameter value
     * @return this
     */
    public Common setQueryParam(String key, Object value) {
        this.queryParams.put(key, value);
        return this;
    }

    /**
     * @return array
     */
    @Override
    public Map toArray() {
        this.setParam(this.field, this.queryParams);
        return super.toArray();
    }

}
