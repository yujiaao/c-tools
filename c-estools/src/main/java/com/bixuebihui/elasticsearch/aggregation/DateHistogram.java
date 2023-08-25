package com.bixuebihui.elasticsearch.aggregation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * Class DateHistogram.
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-datehistogram-aggregation.html">...</a>
 */
public class DateHistogram extends Histogram
{
    public String DEFAULT_TIMEZONE_VALUE = "UTC";

    public static final String[]  CALENDAR_INTERVAL_VALUES ={
            "1m",
            "minute",
            "1h",
            "hour",
            "1d",
            "day",
            "1w",
            "week",
            "1M",
            "month",
            "1q",
            "quarter",
            "1y",
            "year",
    };

    /**
     * @param name     string     $name     the name of this aggregation
     * @param field    string     $field    the name of the field on which to perform the aggregation
     * @param interval int|string $interval the interval by which documents will be bucketed
     */
    public DateHistogram(String name, String field, String interval) {
        super(name, field, interval);
    }

    /**
     * Set the interval by which documents will be bucketed.
     *
     * @param interval int|string
     *
     * @return $this
     */
    public DateHistogram setInterval(String interval)
    {
        if (Arrays.asList(CALENDAR_INTERVAL_VALUES).contains(interval)) {
            return this.setCalendarInterval(interval);
        }
        return  this.setFixedInterval(interval);

    }

    /**
     * @return $this
     */
    public DateHistogram setCalendarInterval(String interval)
    {
        this.setParam("calendar_interval", interval);
        return this;
    }

    /**
     * @return $this
     */
    public DateHistogram setFixedInterval(String interval)
    {
        this.setParam("fixed_interval", interval);
        return this;
    }

    /**
     * Set time_zone option.
     *
     * @return $this
     */
    public DateHistogram setTimezone(String timezone)
    {
        this.setParam("time_zone", timezone);
        return this;
    }

    /**
     * Adjust for granularity of date data.
     *
     * @param factor int $factor set to 1000 if date is stored in seconds rather than milliseconds
     *
     * @return $this
     */
    public DateHistogram setFactor(int factor)
    {
        this.setParam("factor", factor);
        return this;
    }

    /**
     * Set offset option.
     * By default the bucket keys start with 0 and then continue in even spaced steps of interval,
     * e.g. if the interval is 10, the first three buckets (assuming there is data inside them)
     * will be [0, 10), [10, 20), [20, 30). The bucket boundaries can be shifted by using the offset option.
     *
     * This can be best illustrated with an example. If there are 10 documents with values ranging
     * from 5 to 14, using interval 10 will result in two buckets with 5 documents each.
     * If an additional offset 5 is used, there will be only one single bucket [5, 15) containing all the 10 documents.
     * @return $this
     */
    public DateHistogram setOffset(String $offset)
    {
        this.setParam("offset", $offset);
        return this;
    }

    /**
     * Set the format for returned bucket key_as_string values.
     *
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-daterange-aggregation.html#date-format-pattern">...</a>
     *
     * @param format string $format see link for formatting options
     *
     * @return $this
     */
    public DateHistogram setFormat(String format)
    {
        this.setParam("format", format);
        return this;
    }

    /**
     * Set extended bounds option.
     *
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-histogram-aggregation.html#search-aggregations-bucket-histogram-aggregation-extended-bounds">...</a>
     *
     * @param min string $min see link for formatting options
     * @param max string $max see link for formatting options
     *
     * @return $this
     */
    public DateHistogram setExtendedBounds(String min, String max)
    {
        Map bounds = new HashMap();
        bounds.put("min", min);
        bounds.put("max", max);

        if (Long.parseLong(min) > Long.parseLong(max)) {
            bounds.put("min", max);
            bounds.put("max", min);
        }

        this.setParam("extended_bounds", bounds);
        return this;
    }
}
