package com.bixuebihui.elasticsearch.aggregation;

import com.bixuebihui.elasticsearch.query.NameableInterface;
import com.bixuebihui.elasticsearch.query.Params;

import java.util.*;

public abstract class AbstractAggregation extends Params implements NameableInterface
{
    protected String METADATA_KEY = "meta";

    /**
     * @var string The name of this aggregation
     */
    protected String _name;

    /**
     * @var array Subaggregations belonging to this aggregation
     */
    protected Map<String, AbstractAggregation> _aggs = new HashMap<>();

    /**
     * @param $name the name of this aggregation
     */
    public AbstractAggregation(String $name)
    {
        this.setName($name);
    }

    /**
     * Set the name of this aggregation.
     *
     * @return $this
     */
    public AbstractAggregation setName(String $name)
    {
        this._name = $name;

        return this;
    }

    /**
     * Retrieve the name of this aggregation.
     */
    public String getName()
    {
        return this._name;
    }

    /**
     * Retrieve all subaggregations belonging to this aggregation.
     */
    public Map<String,AbstractAggregation>  getAggs()
    {
        return this._aggs;
    }

    /**
     * Add a sub-aggregation.
     *
     * @throws IllegalArgumentException
     *
     * @return $this
     */
    public AbstractAggregation addAggregation(AbstractAggregation aggregation)
    {
        if (aggregation instanceof GlobalAggregation) {
            throw new IllegalArgumentException("Global aggregators can only be placed as top level aggregators");
        }

        this._aggs.put(aggregation._name, aggregation);

        return this;
    }

    /**
     * Add metadata to the aggregation.
     *
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/agg-metadata.html">...</a>
     * @see \Elastica\Aggregation\AbstractAggregation::getMeta()
     * @see \Elastica\Aggregation\AbstractAggregation::clearMeta()
     *
     * @param meta array $meta Metadata to be attached to the aggregation
     *
     * @return $this
     */
    public AbstractAggregation setMeta(List meta)
    {
        if (meta==null) {
            return this.clearMeta();
        }

        this.getRawParams().put(METADATA_KEY, meta);

        return this;
    }

    /**
     * Retrieve the currently configured metadata for the aggregation.
     *
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/agg-metadata.html">...</a>
     * @see \Elastica\Aggregation\AbstractAggregation::setMeta()
     * @see \Elastica\Aggregation\AbstractAggregation::clearMeta()
     */
    public Object getMeta()
    {
        return this.getRawParams().get(METADATA_KEY);
    }

    /**
     * Clears any previously set metadata for this aggregation.
     *
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/agg-metadata.html">...</a>
     * @see \Elastica\Aggregation\AbstractAggregation::setMeta()
     * @see \Elastica\Aggregation\AbstractAggregation::getMeta()
     *
     * @return $this
     */
    public AbstractAggregation clearMeta()
    {
        this.getRawParams().remove(METADATA_KEY);

        return this;
    }

    public Map toArray()
    {
        Map array = super.toArray();

        if (this._aggs.size() > 0) {
            array.put("aggs",  this.convertArrayable( this._aggs));
        }

        return array;
    }

    protected String getBaseName()
    {
        String shortName = super.getBaseName().replaceAll("Aggregation$", "");
        return camelToSnake(shortName);
    }
}
