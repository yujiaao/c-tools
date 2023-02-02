
package com.bixuebihui.jmesa.entry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "key",
    "comparison",
    "value"
})
@Generated("jsonschema2pojo")
public class Filter implements Serializable
{

    @JsonProperty("key")
    private String key;
    @JsonProperty("comparison")
    private String comparison;
    @JsonProperty("value")
    private List<String> value = new ArrayList<String>();
    private final static long serialVersionUID = 2705191131144677890L;

    @JsonProperty("key")
    public String getKey() {
        return key;
    }

    @JsonProperty("key")
    public void setKey(String key) {
        this.key = key;
    }

    @JsonProperty("comparison")
    public String getComparison() {
        return comparison;
    }

    @JsonProperty("comparison")
    public void setComparison(String comparison) {
        this.comparison = comparison;
    }

    @JsonProperty("value")
    public List<String> getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(List<String> value) {
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Filter.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("key");
        sb.append('=');
        sb.append(((this.key == null)?"<null>":this.key));
        sb.append(',');
        sb.append("comparison");
        sb.append('=');
        sb.append(((this.comparison == null)?"<null>":this.comparison));
        sb.append(',');
        sb.append("value");
        sb.append('=');
        sb.append(((this.value == null)?"<null>":this.value));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.comparison == null)? 0 :this.comparison.hashCode()));
        result = ((result* 31)+((this.value == null)? 0 :this.value.hashCode()));
        result = ((result* 31)+((this.key == null)? 0 :this.key.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Filter) == false) {
            return false;
        }
        Filter rhs = ((Filter) other);
        return ((((this.comparison == rhs.comparison)||((this.comparison!= null)&&this.comparison.equals(rhs.comparison)))&&((this.value == rhs.value)||((this.value!= null)&&this.value.equals(rhs.value))))&&((this.key == rhs.key)||((this.key!= null)&&this.key.equals(rhs.key))));
    }

}
