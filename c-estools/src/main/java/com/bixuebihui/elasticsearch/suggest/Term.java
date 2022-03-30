package com.bixuebihui.elasticsearch.suggest;

/**
 * Class Term.
 *
 *  https://www.elastic.co/guide/en/elasticsearch/reference/current/search-suggesters-term.html
 */
public class Term extends Suggest
{
    static final String SORT_SCORE = "score";
    static final String SORT_FREQUENCY = "frequency";

    static final String SUGGEST_MODE_MISSING = "missing";
    static final String SUGGEST_MODE_POPULAR = "popular";
    static final String SUGGEST_MODE_ALWAYS = "always";

    public Term(String name, String field) {
        super(name, field);
    }

    /**
     * @param  analyzer
     *
     * @return this
     */
    public Term setAnalyzer(String analyzer)
    {
        return (Term) this.setParam("analyzer", analyzer);
    }

    /**
     * @param  sort see SORT_* constants for options
     *
     * @return this
     */
    public Term setSort(String sort)
    {
        return (Term) this.setParam("sort", sort);
    }

    /**
     * @param  mode see SUGGEST_MODE_* constants for options
     *
     * @return this
     */
    public Term setSuggestMode(String mode)
    {
        return (Term) this.setParam("suggest_mode", mode);
    }

    /**
     * If true, suggest terms will be lower cased after text analysis.
     *
     * @param  lowercase default true
     *
     * @return this
     */
    public Term setLowercaseTerms(boolean lowercase)
    {
        return (Term) this.setParam("lowercase_terms",  lowercase);
    }

    /**
     * Set the maximum edit distance candidate suggestions can have in order to be considered as a suggestion.
     *
     * @param  max Either 1 or 2. Any other value will result in an error.
     *
     * @return this
     */
    public Term setMaxEdits(int max)
    {
        return (Term) this.setParam("max_edits", (int) max);
    }

    /**
     * The number of minimum prefix characters that must match in order to be a suggestion candidate.
     *
     * @param length defaults to 1
     *
     * @return this
     */
    public Term setPrefixLength(int length)
    {
        return (Term) this.setParam("prefix_len", length);
    }

    /**
     * The minimum length a suggest text term must have in order to be included.
     *
     * @param  length defaults to 4
     *
     * @return this
     */
    public Term setMinWordLength(int length)
    {
        return (Term) this.setParam("min_word_length", length);
    }

    /**
     * @param  max defaults to 5
     *
     * @return this
     */
    public Term setMaxInspections(int max)
    {
        return (Term) this.setParam("max_inspections", max);
    }

    /**
     * Set the minimum number of documents in which a suggestion should appear.
     *
     * @param min |float min Defaults to 0. If the value is greater than 1, it must be a whole number.
     *
     * @return this
     */
    public Term setMinDocFrequency(float min)
    {
        return (Term) this.setParam("min_doc_freq", min);
    }

    /**
     * Set the maximum number of documents in which a suggest text token can exist in order to be included.
     *
     * @param  max
     *
     * @return this
     */
    public Term setMaxTermFrequency(float max)
    {
        return (Term) this.setParam("max_term_freq", max);
    }

    /**
     * Which string distance implementation to use for comparing how similar suggested terms are.
     * Five possible values can be specified:.
     *
     * - internal
     * - damerau_levenshtein
     * - levenshtein
     * - jaro_winkler
     * - ngram
     *
     * @param  distanceAlgorithm string
     *
     * @return this
     */
    public Term setStringDistanceAlgorithm(String distanceAlgorithm)
    {
        return (Term) this.setParam("string_distance", distanceAlgorithm);
    }
}
