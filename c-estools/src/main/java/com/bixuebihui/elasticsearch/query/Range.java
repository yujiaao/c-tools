package com.bixuebihui.elasticsearch.query;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
     * Range query.
     *
     * @author Nicolas Ruflin <spam@ruflin.com>
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-range-query.html
     */
  public  class Range extends Query {
    public static final String GT = "gt";
    public static final String GTE = "gte";
    public static final String LT = "lt";
    public static final String LTE = "lte";
    public static final String FROM = "from";
    public static final String TO = "to";


        /**
         * Constructor.
         *
         * usage:
         *    new Range('age', ['from' => 10, 'to' => 20]);
         *    range.addField('height', ['gte' => 160]);
         *
         * @param fieldName Field name
         * @param args      Field arguments array
         */
        public Range(String fieldName, Map args) {
            if (fieldName != null) {
                this.addField(fieldName, args);
            }
        }
        public Range(){}

        /**
         * Adds a range field to the query.
         *
         * @param fieldName Field name string
         * @param args      Field arguments, array
         * @return this
         */
        public Range addField(String fieldName, Map args) {
            return (Range) this.setParam(fieldName, args);
        }

        public Range between(String fieldName, Object from, Object to ) {
        return (Range) this.setParam(fieldName, ImmutableMap.builder()
                .put(FROM, from).put(TO,to).build());
    }

}
