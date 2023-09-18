package com.bixuebihui.elasticsearch.aggregation;

import java.util.Map;

/**
 * Class Histogram.
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-histogram-aggregation.html">...</a>
 */
class Histogram extends AbstractSimpleAggregation
{
    /**
     * Setting the keyed flag to true associates a unique string key
     * with each bucket and returns the result as a hash rather than an array.
     *
     * @return $this
     */
    public Histogram setKeyed(boolean keyed)
    {
         this.setParam("keyed", keyed);
        return this;
    }

    /**
     * Defines how documents that are missing a value should be treated.
     *
     * @param missing mixed $missing
     *
     * @return $this
     */
    public Histogram setMissing(Object missing)
    {
        this.setParam("missing", missing);
        return this;
    }

    /**
     * @param name string     $name     the name of this aggregation
     * @param field string     $field    the name of the field on which to perform the aggregation
     * @param interval int|string $interval the interval by which documents will be bucketed
     */
    public Histogram(String name, String field, String interval, String version)
    {
        super(name);
        this.setVersion(version);
        this.setField(field);
        this.setInterval(interval);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    String version;


    /**
     * Set the interval by which documents will be bucketed.
     *
     * @param interval int|string
     * @return $this
     */
    public Histogram setInterval(String interval)
    {
        this.setParam("interval", interval);
        return this;
    }

    /**
     * Set the bucket sort order.
     *
     * @param string $order     "_count", "_term", or the name of a sub-aggregation or sub-aggregation response field
     * @param string $direction "asc" or "desc"
     *
     * @return $this
     */
    public Histogram setOrder(String order, String direction)
    {
        this.setParam("order", Map.of(order, direction));
        return this;
    }

    /**
     * Set the minimum number of documents which must fall into a bucket in order for the bucket to be returned.
     *
     * @param count int $count set to 0 to include empty buckets
     *
     * @return $this
     */
    public AbstractSimpleAggregation setMinimumDocumentCount(int count)
    {
        this.setParam("min_doc_count", count);
        return this;
    }
}
