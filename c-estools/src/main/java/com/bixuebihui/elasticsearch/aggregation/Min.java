package com.bixuebihui.elasticsearch.aggregation;

/**
 * Class Min.
 *
 * @see https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-metrics-min-aggregation.html
 */
class Min extends AbstractSimpleAggregation
{
    public Min(String name) {
        super(name);
    }
//    use Traits\MissingTrait;

    public AbstractSimpleAggregation setMissing(Object missing)
    {
        this.setParam("missing", missing);
        return this;
    }
}
