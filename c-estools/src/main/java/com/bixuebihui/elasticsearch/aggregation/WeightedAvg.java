package com.bixuebihui.elasticsearch.aggregation;

import java.util.Map;

/**
 * Class WeightedAvg.
 *
 * @see https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-metrics-weight-avg-aggregation.html
 */
public class WeightedAvg extends AbstractAggregation
{
    /**
     * @param $name the name of this aggregation
     */
    public WeightedAvg(String $name) {
        super($name);
    }

    /**
     * Set the value for this aggregation.
     *
     * @param mixed $missing =null The missing parameter defines how documents that are missing a value should be treated.
     *
     * @return $this
     */
    public WeightedAvg setValue(String $field, Object $missing )
    {
        if (this.hasParam("value") && ((Map)this.getParams().get("value")).get("script")!=null) {
            throw new IllegalArgumentException("Weighted Average aggregation with a value mixing field and script is not possible.");
        }

        Map<String, String> $value = Map.of("field", $field);

        if (null != $missing) {
            $value.put("missing", (String) $missing);
        }
         this.setParam("value", $value);
        return this;
    }

    /**
     * Set the value as a script for this aggregation.
     *
     * @return $this
     */
    public WeightedAvg setValueScript(String $script)
    {
        if (this.hasParam("value") && ((Map)(this.getParams().get("value"))).containsKey("field")) {
            throw new IllegalArgumentException("Weighted Average aggregation with a value mixing field and script is not possible.");
        }

        this.setParam("value", Map.of("script", $script));
        return this;
    }

    /**
     * Set the weight for this aggregation.
     *
     * @param mixed $missing
     *
     * @return $this
     */
    //convert below php code to java
//    public function setWeight(string $field, $missing = null)
//    {
//        if ($this->hasParam('weight') && isset($this->getParam('weight')['script'])) {
//            throw new InvalidException('Weighted Average aggregation with a weight mixing field and script is not possible.');
//        }
//
//        $weight = ['field' => $field];
//
//        if (null !== $missing) {
//            $weight['missing'] = $missing;
//        }
//
//        return $this->setParam('weight', $weight);
//    }
    public WeightedAvg setWeight(String $field, Object $missing)
    {
        if (this.hasParam("weight") && ((Map)(this.getParams().get("weight"))).containsKey("script")) {
            throw new IllegalArgumentException("Weighted Average aggregation with a weight mixing field and script is not possible.");
        }

        Map<String, String> $weight = Map.of("field", $field);

        if (null != $missing) {
            $weight.put("missing", (String) $missing);
        }
        this.setParam("weight", $weight);
        return this;
    }

    /**
     * Set the weight as a script for this aggregation.
     *
     * @return $this
     */
    //convert below php code to java
//    public function setWeightScript(string $script)
//    {
//        if ($this->hasParam('weight') && isset($this->getParam('weight')['field'])) {
//            throw new InvalidException('Weighted Average aggregation with a weight mixing field and script is not possible.');
//        }
//
//        return $this->setParam('weight', ['script' => $script]);
//    }

    public WeightedAvg setWeightScript(String $script)
    {
        if (this.hasParam("weight") && ((Map)(this.getParams().get("weight"))).containsKey("field")) {
            throw new IllegalArgumentException("Weighted Average aggregation with a weight mixing field and script is not possible.");
        }

        this.setParam("weight", Map.of("script", $script));
        return this;
    }
    /**
     * Set the format for this aggregation.
     *
     * @param string $format
     *
     * @return $this
     */
    //convert below php code to java
//    public function setFormat($format)
//    {
//        return $this->setParam('format', $format);
//    }
    public WeightedAvg setFormat(String $format)
    {
         this.setParam("format", $format);
         return this;
    }

    /**
     * Set the value_type for this aggregation.
     *
     * @param mixed $valueType
     *
     * @return $this
     */
    //convert below php code to java
//    public function setValueType($valueType)
//    {
//        return $this->setParam('value_type', $valueType);
//    }
    public WeightedAvg setValueType(String $valueType)
    {
         this.setParam("value_type", $valueType);
         return this;
    }
}
