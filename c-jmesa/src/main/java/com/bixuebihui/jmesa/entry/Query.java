
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
    "maxRows",
    "page",
    "exportType",
    "sort",
    "filter"
})
@Generated("jsonschema2pojo")
public class Query implements Serializable
{

    @JsonProperty("maxRows")
    private Long maxRows;
    @JsonProperty("page")
    private Long page;
    @JsonProperty("exportType")
    private String exportType;
    @JsonProperty("sort")
    private List<Sort> sort = new ArrayList<Sort>();
    @JsonProperty("filter")
    private List<Filter> filter = new ArrayList<Filter>();
    private final static long serialVersionUID = 8752072127684749495L;

    @JsonProperty("maxRows")
    public Long getMaxRows() {
        return maxRows;
    }

    @JsonProperty("maxRows")
    public void setMaxRows(Long maxRows) {
        this.maxRows = maxRows;
    }

    @JsonProperty("page")
    public Long getPage() {
        return page;
    }

    @JsonProperty("page")
    public void setPage(Long page) {
        this.page = page;
    }

    @JsonProperty("exportType")
    public String getExportType() {
        return exportType;
    }

    @JsonProperty("exportType")
    public void setExportType(String exportType) {
        this.exportType = exportType;
    }

    @JsonProperty("sort")
    public List<Sort> getSort() {
        return sort;
    }

    @JsonProperty("sort")
    public void setSort(List<Sort> sort) {
        this.sort = sort;
    }

    @JsonProperty("filter")
    public List<Filter> getFilter() {
        return filter;
    }

    @JsonProperty("filter")
    public void setFilter(List<Filter> filter) {
        this.filter = filter;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Query.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("maxRows");
        sb.append('=');
        sb.append(((this.maxRows == null)?"<null>":this.maxRows));
        sb.append(',');
        sb.append("page");
        sb.append('=');
        sb.append(((this.page == null)?"<null>":this.page));
        sb.append(',');
        sb.append("exportType");
        sb.append('=');
        sb.append(((this.exportType == null)?"<null>":this.exportType));
        sb.append(',');
        sb.append("sort");
        sb.append('=');
        sb.append(((this.sort == null)?"<null>":this.sort));
        sb.append(',');
        sb.append("filter");
        sb.append('=');
        sb.append(((this.filter == null)?"<null>":this.filter));
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
        result = ((result* 31)+((this.filter == null)? 0 :this.filter.hashCode()));
        result = ((result* 31)+((this.maxRows == null)? 0 :this.maxRows.hashCode()));
        result = ((result* 31)+((this.page == null)? 0 :this.page.hashCode()));
        result = ((result* 31)+((this.sort == null)? 0 :this.sort.hashCode()));
        result = ((result* 31)+((this.exportType == null)? 0 :this.exportType.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Query) == false) {
            return false;
        }
        Query rhs = ((Query) other);
        return ((((((this.filter == rhs.filter)||((this.filter!= null)&&this.filter.equals(rhs.filter)))&&((this.maxRows == rhs.maxRows)||((this.maxRows!= null)&&this.maxRows.equals(rhs.maxRows))))&&((this.page == rhs.page)||((this.page!= null)&&this.page.equals(rhs.page))))&&((this.sort == rhs.sort)||((this.sort!= null)&&this.sort.equals(rhs.sort))))&&((this.exportType == rhs.exportType)||((this.exportType!= null)&&this.exportType.equals(rhs.exportType))));
    }

}
