package com.bixuebihui.elasticsearch.query;


import java.util.List;

/**
     * Multi Match.
     *
     * @author Rodolfo Adhenawer Campagnoli Moraes adhenawer@gmail.com
     * @author Wong Wing Lun luiges90@gmail.com
     * @author Tristan Maindron tmaindron@gmail.com
     *
     *  https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-multi-match-query.html
     */
public class MultiMatch extends Query {

    static final String TYPE_BEST_FIELDS = "best_fields";
    static final String TYPE_MOST_FIELDS = "most_fields";
    static final String TYPE_CROSS_FIELDS = "cross_fields";
    static final String TYPE_PHRASE = "phrase";
    static final String TYPE_PHRASE_PREFIX = "phrase_prefix";

    static final String OPERATOR_OR = "or";
    static final String OPERATOR_AND = "and";

    static final String ZERO_TERM_NONE = "none";
    static final String ZERO_TERM_ALL = "all";

    static final String FUZZINESS_AUTO = "AUTO";

        /**
         * Sets the query.
         *
         * @param  query Query default = ""
         *
         * @return this
         */
        public MultiMatch setQuery(String query )
        {
            return (MultiMatch) this.setParam("query", query);
        }

        /**
         * Sets Fields to be used in the query.
         *
         * @param  fields Fields array
         *
         * @return this
         */
        public MultiMatch setFields(List<String> fields)
        {
            return (MultiMatch) this.setParam("fields", fields);
        }

        /**
         * Sets use dis max indicating to either create a dis_max query or a bool query.
         *
         * If not set, defaults to true.
         *
         * @param  useDisMax
         *
         * @return this
         */
        public MultiMatch setUseDisMax(boolean useDisMax)
        {
            return (MultiMatch) this.setParam("use_dis_max", useDisMax);
        }

        /**
         * Sets tie breaker to multiplier value to balance the scores between lower and higher scoring fields.
         *
         * If not set, defaults to 0.0.
         *
         * @param  tieBreaker float
         *
         * @return this
         */
        public MultiMatch setTieBreaker(float tieBreaker)
        {
            return (MultiMatch) this.setParam("tie_breaker", tieBreaker);
        }

        /**
         * Sets operator for Match Query.
         *
         * If not set, defaults to "or"
         *
         * @param  operator
         *
         * @return this
         */
        public MultiMatch setOperator(String operator)
        {
            return (MultiMatch) this.setParam("operator", operator);
        }

        /**
         * Set field minimum should match for Match Query.
         *
         * @param  minimumShouldMatch mixed
         *
         * @return this
         */
        public MultiMatch setMinimumShouldMatch(Object minimumShouldMatch)
        {
            return (MultiMatch) this.setParam("minimum_should_match", minimumShouldMatch);
        }

        /**
         * Set zero terms query for Match Query.
         *
         * If not set, default to "none"
         *
         * @param zeroTermQuery
         *
         * @return this
         */
        public MultiMatch setZeroTermsQuery(String zeroTermQuery)
        {
            return (MultiMatch) this.setParam("zero_terms_query", zeroTermQuery);
        }

        /**
         * Set cutoff frequency for Match Query.
         *
         * @param  cutoffFrequency
         *
         * @return this
         */
        public MultiMatch setCutoffFrequency(float cutoffFrequency)
        {
            return (MultiMatch) this.setParam("cutoff_frequency", cutoffFrequency);
        }

        /**
         * Set type.
         *
         * @param  type
         *
         * @return this
         */
        public MultiMatch setType(String type)
        {
            return (MultiMatch) this.setParam("type", type);
        }

        /**
         * Set fuzziness.
         *
         * @param fuzziness float|string
         *
         * @return this
         */
        public MultiMatch setFuzziness(float fuzziness)
        {
            return (MultiMatch) this.setParam("fuzziness", fuzziness);
        }

        /**
         * Set prefix length.
         *
         * @param prefixLength
         *
         * @return this
         */
        public MultiMatch setPrefixLength(int prefixLength)
        {
            return (MultiMatch) this.setParam("prefix_length", (int) prefixLength);
        }

        /**
         * Set max expansions.
         *
         * @param  maxExpansions
         *
         * @return this
         */
        public MultiMatch setMaxExpansions(int maxExpansions)
        {
            return (MultiMatch) this.setParam("max_expansions", maxExpansions);
        }

        /**
         * Set analyzer.
         *
         * @param analyzer
         *
         * @return this
         */
        public MultiMatch setAnalyzer(String analyzer)
        {
            return (MultiMatch) this.setParam("analyzer", analyzer);
        }
    }
