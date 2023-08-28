package com.bixuebihui.elasticsearch.aggregation;

import com.bixuebihui.elasticsearch.script.AbstractScript;
import com.bixuebihui.elasticsearch.script.ScriptFields;

import java.util.Map;

/**
 * Class TopHits.
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-metrics-top-hits-aggregation.html">...</a>
 */
public class TopHits extends AbstractAggregation
{
    /**
     * @param $name the name of this aggregation
     */
    public TopHits(String $name) {
        super($name);
    }

    public Map toArray()
    {
        Map arr = super.toArray();
        if(arr.get("top_hits")==null)
        {
            arr.put("top_hits", new Object());
        }

        return arr;
    }


    /**
     * The maximum number of top matching hits to return per bucket. By default the top three matching hits are returned.
     *
     * @return $this
     */
    //convert below php code to java
//    public function setSize(int $size): self
//    {
//        return $this->setParam('size', $size);
//    }
    public TopHits setSize(int size)
    {
         this.setParam("size", size);
         return this;
    }

    /**
     * The offset from the first result you want to fetch.
     *
     * @return $this
     */
    //convert below php code to java
//    public function setFrom(int $from): self
//    {
//        return $this->setParam('from', $from);
//    }
    public TopHits setFrom(int from)
    {
         this.setParam("from", from);
         return this;
    }

    /**
     * How the top matching hits should be sorted. By default the hits are sorted by the score of the main query.
     *
     * @return $this
     */
    //convert below php code to java
//    public function setSort(array $sortArgs): self
//    {
//        return $this->setParam('sort', $sortArgs);
//    }
    public TopHits setSort(Map sortArgs)
    {
         this.setParam("sort", sortArgs);
         return this;
    }

    /**
     * Allows to control how the _source field is returned with every hit.
     *
     * @param params array|bool|string $params Fields to be returned or false to disable source
     *
     * @return $this
     */
    //convert below php code to java
//    public function setSource($params): self
//    {
//        return $this->setParam('_source', $params);
//    }
    public TopHits setSource(Object params)
    {
         this.setParam("_source", params);
         return this;
    }

    /**
     * Returns a version for each search hit.
     *
     * @return $this
     */
    public TopHits setVersion(boolean version)
    {
         this.setParam("version", version);
         return this;
    }

    /**
     * Enables explanation for each hit on how its score was computed.
     *
     * @return $this
     */
    //convert below php code to java
//    public function setExplain(bool $explain): self
//    {
//        return $this->setParam('explain', $explain);
//    }
    public TopHits setExplain(boolean explain)
    {
         this.setParam("explain", explain);
         return this;
    }

    /**
     * Set script fields.
     *
     * @param scriptFields array|ScriptFields
     *
     * @return $this
     */

    public TopHits setScriptFields(Object scriptFields)
    {
         if (scriptFields instanceof Map) {
             scriptFields = new ScriptFields((Map)scriptFields);
         }
         this.setParam("script_fields", scriptFields);
         return this;
    }

    /**
     * Adds a Script to the aggregation.
     *
     * @return $this
     */
    public TopHits addScriptField(String name, AbstractScript script)
    {
         if (!this.getParams().containsKey("script_fields")) {
             this.getParams().put("script_fields", new ScriptFields(null));
         }
         ((ScriptFields)this.getParams().get("script_fields")).addScript(name, script);
         return this;
    }

    /**
     * Sets highlight arguments for the results.
     *
     * @return $this
     */
    public TopHits setHighlight(Map $highlightArgs)
    {
         this.setParam("highlight", $highlightArgs);
         return this;
    }

    /**
     * Allows to return the field data representation of a field for each hit.
     *
     * @return $this
     */
    public TopHits setFieldDataFields(Map $fields)
    {
         this.setParam("docvalue_fields", $fields);
         return this;
    }
}
