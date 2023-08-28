package com.bixuebihui.elasticsearch.aggregation;

import java.util.Map;

import static java.util.Collections.emptyMap;

/**
 * Class GlobalAggregation.
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-global-aggregation.html">...</a>
 */
public class GlobalAggregation extends AbstractAggregation
{
    /**
     * @param name the name of this aggregation
     */
    public GlobalAggregation(String name) {
        super(name);
    }

    public Map toArray()
    {
        Map array = super.toArray();
        // Force json encoding to object
        array.put(this.getBaseName(), emptyMap());

        return array;
    }
}
