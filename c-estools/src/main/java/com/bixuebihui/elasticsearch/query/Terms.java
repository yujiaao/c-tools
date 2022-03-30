package com.bixuebihui.elasticsearch.query;



import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

/**
 * Terms query.
 *
 * @author Nicolas Ruflin spam@ruflin.com
 * @author Roberto Nygaard roberto@nygaard.es
 *
 *  https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-terms-query.html
 */
public class Terms extends Query
{
    /**
     * Terms.
     *
     * @var array Terms
     */
    protected List terms;

    /**
     * Terms key.
     *
     * @var string Terms key
     */
    protected String key;

    /**
     * Construct terms query.
     *
     * @param  key   OPTIONAL Terms key
     * @param  terms OPTIONAL Terms list
     */
    public Terms(String key, List terms)
    {
        this.setTerms(key, terms);
    }

    /**
     * Sets key and terms for the query.
     *
     * @param  key   Terms key
     * @param  terms terms for the query
     *
     * @return this
     */
    public Terms setTerms(String key, List terms)
    {
        this.key = key;
        this.terms = terms;

        return this;
    }

    /**
     * Sets key and terms lookup for the query.
     *
     * @param  key         Terms key
     * @param   termsLookup terms lookup for the query
     *
     * @return this
     */
    public Terms setTermsLookup(String key, List termsLookup)
    {
        this.key = key;
        this.terms = termsLookup;

        return this;
    }

    /**
     * Adds a single term to the list.
     *
     * @param  term Term
     *
     * @return this
     */
    public Terms addTerm(String term)
    {
        if(this.terms ==null){
            this.terms = Lists.newArrayList();
        }
        this.terms.add(term);

        return this;
    }

    /**
     * Sets the minimum matching values.
     *
     * @param minimum int|string  Minimum value
     *
     * @return this
     */
    public Terms setMinimumMatch(int minimum)
    {
        return (Terms) this.setParam("minimum_match", minimum);
    }

    /**
     * Converts the terms object to an array.
     *
     * @see Query::toArray()
     *
     * @throws IllegalArgumentException If term key is empty
     *
     * @return array Query array
     */
    @Override
    public Map toArray()
    {
        if(this.key ==null){
            throw new IllegalArgumentException("term key is empty ");
        }
        this.setParam(this.key, this.terms);

        return super.toArray();
    }
}
