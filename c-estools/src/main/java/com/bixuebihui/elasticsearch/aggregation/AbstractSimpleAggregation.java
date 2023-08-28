package com.bixuebihui.elasticsearch.aggregation;

import java.util.HashMap;
import java.util.Map;

abstract class AbstractSimpleAggregation extends AbstractAggregation
{
    public AbstractSimpleAggregation(String name) {
        super(name);
    }

    /**
     * Set the field for this aggregation.
     *
     * @param field string $field the name of the document field on which to perform this aggregation
     *
     * @return $this
     */
    public AbstractSimpleAggregation setField(String field)
    {
        this.setParam("field", field);
        return this;
    }

    /**
     * Set a script for this aggregation.
     *
     * @param \Elastica\Script\AbstractScript|string $script
     *
     * @return $this
     */
    public AbstractSimpleAggregation setScript(String script)
    {
        this.setParam("script", script);
        return this;
    }

    /**
     * {@inheritdoc}
     */
    public Map toArray()
    {
        if (!this.hasParam("field") && !this.hasParam("script")) {
            throw new IllegalArgumentException("Either the field param or the script param should be set");
        }
        Map array = super.toArray();
        String baseName = this.getBaseName();

        Map baseMap = (Map) (array).get(baseName);

        Object obj = baseMap.get("script");
        if (obj instanceof Map) {
            Map script = (Map) obj;
            baseMap.remove("script");
            HashMap h = new HashMap<>(baseMap);
            h.putAll(script);
            array.put(baseName,  h);
        }

        return array;
    }
}
