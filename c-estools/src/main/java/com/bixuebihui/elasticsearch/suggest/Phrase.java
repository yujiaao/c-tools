package com.bixuebihui.elasticsearch.suggest;


import java.util.Map;

public class Phrase extends Suggest {
        public Phrase(String name, String field) {
            super(name, field);
        }

        /**
         * @param analyzer
         * @return this
         */
        public Phrase setAnalyzer(String analyzer) {
            return (Phrase) this.setParam("analyzer", analyzer);
        }

        /**
         * Set the max size of the n-grams (shingles) in the field.
         *
         * @param size
         * @return this
         */
        public Phrase setGramSize(int size) {
            return (Phrase) this.setParam("gram_size", size);
        }

        /**
         * Set the likelihood of a term being misspelled even if the term exists in the dictionary.
         *
         * @param likelihood Defaults to 0.95, meaning 5% of the words are misspelled.
         * @return this
         */
        public Phrase setRealWordErrorLikelihood(float likelihood) {
            return (Phrase) this.setParam("real_word_error_likelihood", likelihood);
        }

        /**
         * Set the factor applied to the input phrases score to be used as a threshold for other suggestion candidates.
         * Only candidates which score higher than this threshold will be included in the result.
         *
         * @param confidence Defaults to 1.0.
         * @return this
         */
        public Phrase setConfidence(float confidence) {
            return (Phrase) this.setParam("confidence", confidence);
        }

        /**
         * Set the maximum percentage of the terms considered to be misspellings in order to form a correction.
         *
         * @param max
         * @return this
         */
        public Phrase setMaxErrors(float max) {
            return (Phrase) this.setParam("max_errors", max);
        }

        /**
         * @param separator
         * @return this
         */
        public Phrase setSeparator(String separator) {
            return (Phrase) this.setParam("separator", separator);
        }

        /**
         * Set suggestion highlighting.
         *
         * @param preTag
         * @param postTag
         * @return this
         */
        public Phrase setHighlight(String preTag, String postTag) {
            return (Phrase) this.setParam("highlight", Map.of("pre_tag", preTag,
                    "post_tag", postTag));
        }

        /**
         * @param discount default 0.4
         * @return this
         */
        public Phrase setStupidBackoffSmoothing(float discount) {
            return this.setSmoothingModel("stupid_backoff", Map.of(
                    "discount", discount));
        }

        /**
         * @param alpha, default to 0.5
         * @return this
         */
        public Phrase setLaplaceSmoothing(float alpha) {
            return this.setSmoothingModel("laplace", Map.of(
                    "alpha", alpha));
        }

        /**
         * @param trigramLambda float
         * @param bigramLambda  float
         * @param unigramLambda float
         * @return this
         */
        public Phrase setLinearInterpolationSmoothing(float trigramLambda, float bigramLambda, float unigramLambda) {
            return this.setSmoothingModel("linear_interpolation",
                    Map.of("trigram_lambda", trigramLambda,
                            "bigram_lambda", bigramLambda,
                            "unigram_lambda", unigramLambda));
        }

        /**
         * @param model  the name of the smoothing model
         * @param params map
         * @return this
         */
        public Phrase setSmoothingModel(String model, Map params) {
            return (Phrase) this.setParam("smoothing", Map.of(
                    model, params));
        }

        /**
         * @param generator DirectGenerator
         * @return this
         */
        public Phrase addCandidateGenerator(DirectGenerator generator) {
            return (Phrase) this.setParam("candidate_generator", generator);
        }

        /**
         * {@inheritdoc}
         */
        @Override
        public Map toArray() {
            Map array = super.toArray();

            String baseName = this.getBaseName();

            Map map = (Map) array.get(baseName);

            // 扁平化
            if (map.containsKey("candidate_generator") &&
                    map.get("candidate_generator") instanceof Map ) {
                Map generator = (Map) map.get("candidate_generator");
                map.remove("candidate_generator");

                Object key = generator.keySet().toArray()[0];
                Object value = generator.get(key);

                map.put(key, value);
            }

            return array;
        }
    }
