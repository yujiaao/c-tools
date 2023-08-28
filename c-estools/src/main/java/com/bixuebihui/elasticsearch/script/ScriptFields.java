package com.bixuebihui.elasticsearch.script;

import com.bixuebihui.elasticsearch.query.Params;

import java.util.HashMap;
import java.util.Map;

/**
 * Container for scripts as fields.
 *
 * @author Sebastien Lavoie <github@lavoie.sl>
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-script-fields.html">...</a>
 */
public class ScriptFields extends Params
{
    /**
     * @param $scripts array<string, AbstractScript>  OPTIONAL
     */
    public ScriptFields(Map $scripts )
    {
        if ($scripts!=null) {
            this.setScripts($scripts);
        }
    }

    /**
     * @param $name string  Name of the Script field
     *
     * @throws IllegalArgumentException
     *
     * @return $this
     */
    public ScriptFields addScript(String $name, AbstractScript $script)
    {
        if ($name.length()<=0) {
            throw new IllegalArgumentException("The name of a Script is required and must be a string");
        }
        this.setParam($name, $script);

        return this;
    }

    /**
     * @param scripts array<string, AbstractScript>  Associative array of string => Elastica\Script\Script
     *
     * @return $this
     */

    public ScriptFields setScripts(Map<String, AbstractScript> scripts)
    {
        this.getParams();
        for (Map.Entry<String, AbstractScript> entry : scripts.entrySet()) {
            this.addScript(entry.getKey(), entry.getValue());
        }

        return this;
    }

    public Map toArray()
    {
        return this.convertArrayable(this.getParams());
    }
}
