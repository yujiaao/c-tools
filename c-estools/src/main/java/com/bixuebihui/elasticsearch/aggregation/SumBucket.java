package com.bixuebihui.elasticsearch.aggregation;

/**
 * Class SumBucket.
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-pipeline-sum-bucket-aggregation.html">...</a>
 */
public class SumBucket extends AbstractAggregation implements GapPolicyInterface {
    public SumBucket(String $name, String $bucketsPath) {
        super($name);

        this.setBucketsPath($bucketsPath);
    }


    public SumBucket setBucketsPath(String $bucketsPath) {
        this.setParam("buckets_path", $bucketsPath);
        return this;
    }

    public SumBucket setGapPolicy(String $gapPolicy)// = GapPolicyInterface::SKIP)
    {
        this.setParam("gap_policy", $gapPolicy);
        return this;
    }

/**
     * Set the format for this aggregation.
     *
     * @return $this
     */
    public SumBucket setFormat(String $format)
    {
         this. setParam("format", $format);
         return this;
    }
}
