package com.bixuebihui.elasticsearch.aggregation;

/**
 * Class Max.
 *
 * @see https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-metrics-max-aggregation.html
 */
class Max extends AbstractSimpleAggregation
{
    public Max(String name) {
        super(name);
    }
    public AbstractSimpleAggregation setMissing(Object missing)
    {
        this.setParam("missing", missing);
        return this;
    }
//    use Traits\MissingTrait;
}
