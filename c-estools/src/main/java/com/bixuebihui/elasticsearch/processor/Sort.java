package com.bixuebihui.elasticsearch.processor;

import com.bixuebihui.elasticsearch.query.Params;

/**
 * Elastica Sort Processor.
 *
 * @author   Federico Panini <fpanini@gmail.com>
 *
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/sort-processor.html
 */
public class Sort extends Params
{
    /**
     * Split constructor.
     *
     * @param field String
     */
    public Sort(String field)
    {
        this.setField(field);
    }

    /**
     * Set the field.
     *
     * @param  field string
     *
     * @return this
     */
    public Sort setField(String field)
    {
        return (Sort) this.setParam("field", field);
    }

    /**
     * Set order. Default "asc".
     *
     * @param  order string
     *
     * @return this
     */
    public Sort setOrder(String order)
    {
        return (Sort) this.setParam("order", order);
    }
}
