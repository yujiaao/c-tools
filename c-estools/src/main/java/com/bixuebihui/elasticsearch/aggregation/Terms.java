package com.bixuebihui.elasticsearch.aggregation;

import java.util.Map;

/**
 * Class Terms.
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-terms-aggregation.html">...</a>
 */
public class Terms extends AbstractTermsAggregation implements  MissingTrait
{
    public Terms(String name) {
        super(name);
    }

    //use Traits\MissingTrait;

    Terms setMissing(Object missing)
    {
        this.setParam("missing", missing);
        return this;
    }

    /**
     * Set the bucket sort order.
     *
     * @param order string $order     "_count", "_term", or the name of a sub-aggregation or sub-aggregation response field
     * @param direction string $direction "asc" or "desc"
     *
     * @return $this
     *
     */
    //convert below php code to java
    public Terms setOrder(String order, String direction)
    {
        this.setParam("order", Map.of(order, direction));
        return this;
    }

    /**
     * Sets a list of bucket sort orders.
     *
     * @param orders array $orders a list of [<aggregationField>|"_count"|"_term" => <direction>] definitions
     *
     * @return $this
     */
    public Terms setOrders(Map orders)
    {
        this.setParam("order", orders);
        return this;
    }


    /**
     * For Composite Agg. Include in the response documents without a value for a given source.
     *
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/7.17/search-aggregations-bucket-composite-aggregation.html#_missing_bucket">...</a>
     *
     * @return $this
     */
    public Terms setMissingBucket()
    {
        this.setParam("missing_bucket", true);
        return this;
    }



}
