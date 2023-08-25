package com.bixuebihui.elasticsearch.aggregation;

import java.util.List;
import java.util.Map;

/**
 * Class AbstractTermsAggregation.
 */
public abstract class AbstractTermsAggregation extends AbstractSimpleAggregation
{
    public AbstractTermsAggregation(String name) {
        super(name);
    }
    //use Traits\ShardSizeTrait;
    /**
     * @return $this
     */
    public AbstractTermsAggregation setShardSize(int $size)
{
    this.setParam("shard_size", $size);
    return this;
}

    public String EXECUTION_HINT_MAP = "map";
    public String EXECUTION_HINT_GLOBAL_ORDINALS = "global_ordinals";

    /**
     * Set the minimum number of documents in which a term must appear in order to be returned in a bucket.
     *
     * @return $this
     */
    public AbstractTermsAggregation setMinimumDocumentCount(int $count)
    {
        this.setParam("min_doc_count", $count);
        return this;
    }

    /**
     * Filter documents to include based on a regular expression.
     *
     * @param string $pattern a regular expression, following the Regexp syntax
     * @return $this
     * @see https://www.elastic.co/guide/en/elasticsearch/reference/current/regexp-syntax.html for syntax
     */
    public AbstractTermsAggregation setInclude(String $pattern)
    {
        this.setParam("include", $pattern);
        return this;
    }

    /**
     * Filter documents to include based on a list of exact values.
     *
     * @param string[] $values
     * @return $this
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-terms-aggregation.html#_filtering_values_with_exact_values_2">...</a>
     */
    public AbstractTermsAggregation setIncludeAsExactMatch(Map $values)
    {
        this.setParam("include", $values);
        return this;
    }

    /**
     * Set the aggregation filter to use partitions.
     *
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-terms-aggregation.html#_filtering_values_with_partitions">...</a>
     *
     * @return $this
     */
    public AbstractTermsAggregation setIncludeWithPartitions(int $partition, int $numPartitions)
    {
        //convert below php code to java
//         $this->setParam('include', [
//            'partition' => $partition,
//            'num_partitions' => $numPartitions,
//        ]);
        Map include = Map.of("partition", $partition, "num_partitions", $numPartitions);
        this.setParam("include", include);
        return this;
    }

    /**
     * Filter documents to exclude based on a regular expression.
     *
     * @param $pattern string $pattern a regular expression
     * @return $this
     */
    public AbstractTermsAggregation setExclude(String $pattern)
    {
        //convert below php code to java
        // $this->setParam('exclude', $pattern);
        this.setParam("exclude", $pattern);
        return this;
    }

    /**
     * Filter documents to exclude based on a list of exact values.
     *
     * @param $values string[] $values
     * @return $this
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-terms-aggregation.html#_filtering_values_with_exact_values_2">...</a>
     */
    public AbstractTermsAggregation setExcludeAsExactMatch(List<String> $values)
    {
        //convert below php code to java
        //$this->setParam('exclude', $values);
        this.setParam("exclude", $values);
        return this;
    }

    /**
     * Sets the amount of terms to be returned.
     *
     * @return $this
     */
    public AbstractTermsAggregation setSize(int $size)
    {
        //convert below php code to java
        // $this->setParam('size', $size);
        this.setParam("size", $size);
        return this;
    }

    /**
     * Instruct Elasticsearch to use direct field data or ordinals of the field values to execute this aggregation.
     * The execution hint will be ignored if it is not applicable.
     *
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-terms-aggregation.html#search-aggregations-bucket-terms-aggregation-execution-hint">...</a>
     *
     * @param hint string $hint Execution hint, use one of self::EXECUTION_HINT_MAP or self::EXECUTION_HINT_GLOBAL_ORDINALS
     *
     * @return $this
     */
    public AbstractTermsAggregation setExecutionHint(String hint)
    {
        //convert below php code to java
        // $this->setParam('execution_hint', $hint);
        this.setParam("execution_hint", hint);
        return this;

    }
}
