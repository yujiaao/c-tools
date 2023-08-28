package com.bixuebihui.elasticsearch.aggregation;

/**
 * Class ValueCount.
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-metrics-valuecount-aggregation.html">...</a>
 */
public  class ValueCount extends AbstractAggregation
{
    /**
     * @param string $name  the name of this aggregation
     * @param string $field the field on which to perform this aggregation
     */
    public ValueCount(String $name, String $field)
    {
        super($name);
        this.setField($field);
    }

    /**
     * Set the field for this aggregation.
     *
     * @param string $field the name of the document field on which to perform this aggregation
     *
     * @return $this
     */
    public ValueCount setField(String $field)
    {
         this.setParam("field", $field);
         return this;
    }
}
