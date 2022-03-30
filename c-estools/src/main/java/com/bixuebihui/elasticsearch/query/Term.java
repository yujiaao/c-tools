package com.bixuebihui.elasticsearch.query;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author xwx
 */
public class Term extends Query {

        /**
         * Constructs the Term query object.
         *
         * @param term map term OPTIONAL Calls setTerm with the given term array
         */
        public Term(Map term) {
            if(term==null){
                term = Maps.newHashMap();
            }
            this.setRawTerm(term);
        }

        public Term(String key, Object value) {
            super();
            this.setTerm(key, value,(float) 1.0);
        }

        /**
         * Set term can be used instead of addTerm if some more special
         * values for a term have to be set.
         *
         * @param term map Term array
         * @return this
         */
        public Term setRawTerm(Map term) {
            return (Term) this.setParams(term);
        }

        /**
         * Adds a term to the term query.
         *
         * @param key   Key to query
         * @param value string|array  Values(s) for the query. Boost can be set with array
         * @param boost OPTIONAL Boost value (default = 1.0)
         * @return this
         */
        public Term setTerm(String key, Object value, float boost) {
            return this.setRawTerm(
                    ImmutableMap.<String, Map>builder()
                            .put(key, ImmutableMap.<String, Object>builder()
                                    .put("value", value)
                                    .put("boost", boost)
                                    .build()
                            ).build()
            );
        }
    }
