package com.bixuebihui.elasticsearch.suggest;

import com.bixuebihui.elasticsearch.query.Params;

/**
     * Class DirectGenerator.
     * <p>
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/search-suggesters-phrase.html#_direct_generators
 * @author xwx
 */
   public class DirectGenerator extends Params {
        static final String SUGGEST_MODE_MISSING = "missing";
        static final String SUGGEST_MODE_POPULAR = "popular";
        static final String SUGGEST_MODE_ALWAYS = "always";

        /**
         * @param field
         */
        public DirectGenerator(String field) {
            this.setField(field);
        }

        /**
         * Set the field name from which to fetch candidate suggestions.
         *
         * @param field
         * @return this
         */
        public DirectGenerator setField(String field) {
            return (DirectGenerator) this.setParam("field", field);
        }

        /**
         * Set the maximum corrections to be returned per suggest text token.
         *
         * @param size int
         * @return this
         */
        public DirectGenerator setSize(int size) {
            return (DirectGenerator) this.setParam("size", size);
        }

        /**
         * @param mode see SUGGEST_MODE_* constants for options
         * @return this
         */
        public DirectGenerator setSuggestMode(String mode) {
            return (DirectGenerator) this.setParam("suggest_mode", mode);
        }

        /**
         * @param max can only be a value between 1 and 2. Defaults to 2.
         * @return this
         */
        public DirectGenerator setMaxEdits(int max) {
            return (DirectGenerator) this.setParam("max_edits", max);
        }

        /**
         * @param length defaults to 1
         * @return this
         */
        public DirectGenerator setPrefixLength(int length) {
            return (DirectGenerator) this.setParam("prefix_length", length);
        }

        /**
         * @param min defaults to 4
         * @return this
         */
        public DirectGenerator setMinWordLength(int min) {
            return (DirectGenerator) this.setParam("min_word_length", min);
        }

        /**
         * @param max
         * @return this
         */
        public DirectGenerator setMaxInspections(int max) {
            return (DirectGenerator) this.setParam("max_inspections", max);
        }

        /**
         * @param min float
         * @return this
         */
        public DirectGenerator setMinDocFrequency(float min) {
            return (DirectGenerator) this.setParam("min_doc_freq", min);
        }

        /**
         * @param max float
         * @return this
         */
        public DirectGenerator setMaxTermFrequency(float max) {
            return (DirectGenerator) this.setParam("max_term_freq", max);
        }

        /**
         * Set an analyzer to be applied to the original token prior to candidate generation.
         *
         * @param pre an analyzer string
         * @return this
         */
        public DirectGenerator setPreFilter(String pre) {
            return (DirectGenerator) this.setParam("pre_filter", pre);
        }

        /**
         * Set an analyzer to be applied to generated tokens before they are passed to the phrase scorer.
         *
         * @param post string
         * @return this
         */
        public DirectGenerator setPostFilter(String post) {
            return (DirectGenerator) this.setParam("post_filter", post);
        }
    }
