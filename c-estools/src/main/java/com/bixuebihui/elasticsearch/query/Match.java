package com.bixuebihui.elasticsearch.query;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xwx
 */
public class Match extends Query {

        static final String ZERO_TERM_NONE = "none";
        static final String ZERO_TERM_ALL = "all";
        static final String FUZZINESS_AUTO = "AUTO";

        /**
         * @param field  string
         * @param values mixed
         */
        public Match(String field, Object values) {
            if (null != field && null != values) {
                this.setParam(field, values);
            }
        }

        /**
         * Sets a param for the message array.
         *
         * @param field  string
         * @param values mixed
         * @return this
         */
        public Match setField(String field, Object values) {
            return (Match) this.setParam(field, values);
        }

        /**
         * Sets a param for the given field.
         *
         * @param field string
         * @param key   string
         * @param value string, int or float
         * @return this
         */
        public Match setFieldParam(String field, String key, Object value) {
            if (!this.getParams().containsKey(field)) {
                this.getParams().put(field, new HashMap<>(8));
            }

            ((Map) this.getParams().get(field)).put(key, value);

            return this;
        }

        /**
         * Sets the query string.
         *
         * @param field string
         * @param query string
         * @return this
         */
        public Match setFieldQuery(String field, String query) {
            return this.setFieldParam(field, "query", query);
        }

        /**
         * Set field operator.
         *
         * @param field
         * @param operator
         * @return this
         */
        public Match setFieldOperator(String field, String operator) {
            this.setFieldParam(field, "operator", operator == null ? OPERATOR_OR : operator);
            return this;
        }

        /**
         * Set field analyzer.
         *
         * @param field    string
         * @param analyzer string
         * @return this
         */
        public Match setFieldAnalyzer(String field, String analyzer) {
            return this.setFieldParam(field, "analyzer", analyzer);
        }

        /**
         * Set field boost value.
         * <p>
         * If not set, defaults to 1.0.
         *
         * @param field string
         * @param boost float default 1.0
         * @return this
         */
        public Match setFieldBoost(String field, float boost) {
            return this.setFieldParam(field, "boost", boost);
        }

        /**
         * Set field minimum should match.
         *
         * @param field              string
         * @param minimumShouldMatch int|string
         * @return this
         * <p>
         * Possible values for minimum_should_match https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-minimum-should-match.html
         */
        public Match setFieldMinimumShouldMatch(String field, int minimumShouldMatch) {
            return this.setFieldParam(field, "minimum_should_match", minimumShouldMatch);
        }

        /**
         * Set field fuzziness.
         *
         * @param field     string
         * @param fuzziness mixed
         * @return this
         */
        public Match setFieldFuzziness(String field, Object fuzziness) {
            return this.setFieldParam(field, "fuzziness", fuzziness);
        }

        /**
         * Set field fuzzy rewrite.
         *
         * @param field        string
         * @param fuzzyRewrite string
         * @return this
         */
        public Match setFieldFuzzyRewrite(String field, String fuzzyRewrite) {
            return this.setFieldParam(field, "fuzzy_rewrite", fuzzyRewrite);
        }

        /**
         * Set field prefix length.
         *
         * @param field        string
         * @param prefixLength int
         * @return this
         */
        public Match setFieldPrefixLength(String field, int prefixLength) {
            return this.setFieldParam(field, "prefix_length", prefixLength);
        }

        /**
         * Set field max expansions.
         *
         * @param field         string
         * @param maxExpansions int
         * @return this
         */
        public Match setFieldMaxExpansions(String field, int maxExpansions) {
            return this.setFieldParam(field, "max_expansions", maxExpansions);
        }

        /**
         * Set zero terms query.
         * <p>
         * If not set, default to "none"
         *
         * @param field         string
         * @param zeroTermQuery string default ZERO_TERM_NONE
         * @return this
         */
        public Match setFieldZeroTermsQuery(String field, String zeroTermQuery) {
            return this.setFieldParam(field, "zero_terms_query", zeroTermQuery);
        }

        /**
         * Set cutoff frequency.
         *
         * @param field           string
         * @param cutoffFrequency float
         * @return this
         */
        public Match setFieldCutoffFrequency(String field, float cutoffFrequency) {
            return this.setFieldParam(field, "cutoff_frequency", cutoffFrequency);
        }
    }
