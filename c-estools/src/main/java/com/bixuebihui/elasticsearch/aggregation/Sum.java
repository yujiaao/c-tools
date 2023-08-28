package com.bixuebihui.elasticsearch.aggregation;

/**
 * Class Sum.
 *
 * @see https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-metrics-sum-aggregation.html
 */
public class Sum extends AbstractSimpleAggregation
{
    public Sum(String name) {
        super(name);
    }

    //use Traits\MissingTrait;
    Sum setMissing(Object missing)
    {
        this.setParam("missing", missing);
        return this;
    }
}
