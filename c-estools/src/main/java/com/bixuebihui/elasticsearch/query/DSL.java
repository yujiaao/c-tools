package com.bixuebihui.elasticsearch.query;

/**
 * @author xwx
 */

public enum DSL {
    /**
     * for plain search
     */
    TYPE_QUERY,
    /**
     * for aggregation, not implemented yet
     * TODO
     */
    TYPE_AGGREGATION,

    /**
     * TODO
     */
    TYPE_SUGGEST
}
