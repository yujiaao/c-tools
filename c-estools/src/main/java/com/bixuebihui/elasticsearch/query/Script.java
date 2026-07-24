package com.bixuebihui.elasticsearch.query;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Script query.
 *
 * <p>https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-script-query.html</p>
 *
 * <pre>
 * {
 *     "script": {
 *         "script": {
 *             "source": "doc['new_car_series_names'].size() == 0"
 *         }
 *     }
 * }
 * </pre>
 */
public class Script extends Query {

    public Script(String source) {
        this(source, Maps.newHashMap(), null);
    }

    public Script(String source, Map<String, Object> params, String lang) {
        super();
        Map<String, Object> script = Maps.newHashMap();
        script.put("source", source);
        if (params != null && !params.isEmpty()) {
            script.put("params", params);
        }
        if (lang != null) {
            script.put("lang", lang);
        }
        this.setParam("script", script);
    }
}
