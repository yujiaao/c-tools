package com.bixuebihui.elasticsearch.aggregation;

import java.util.HashMap;

/**
 * Class Range.
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-range-aggregation.html">...</a>
 */
public class Range extends AbstractSimpleAggregation
{
    public Range(String name) {
        super(name);
    }

    public Range setKeyed(boolean keyed)
    {
        this.setParam("keyed", keyed);
        return this;
    }

    /**
     * Add a range to this aggregation.
     *
     * @param float|int|string|null $fromValue low end of this range, exclusive (greater than or equal to)
     * @param float|int|string|null $toValue   high end of this range, exclusive (less than)
     * @param string|null           $key       customized key value
     *
     * @throws InvalidException
     *
     * @return $this
     */
    public Range addRange(Object $fromValue, Object $toValue, String $key)
    {
        if (null == $fromValue && null == $toValue) {
            throw new IllegalArgumentException("Either fromValue or toValue must be set. Both cannot be null.");
        }

        //convert below php code to java

        HashMap<String, Object> range = new HashMap<>();
        if (null != $fromValue) {
            range.put("from", $fromValue);
        }
        if (null != $toValue) {
            range.put("to", $toValue);
        }
        if (null != $key) {
            range.put("key", $key);
        }
        this.addParam("ranges", range);

        return this;
    }
}
