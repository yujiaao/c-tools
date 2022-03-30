package com.bixuebihui.elasticsearch.suggest;


import com.bixuebihui.elasticsearch.query.DSL;
import com.bixuebihui.elasticsearch.query.Params;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class Suggest extends Params {
        String name;
        String field;

        public Suggest(String name, String field) {
            this.name = name;
            this.field = field;
        }

        public DSL getType() {
            return DSL.TYPE_SUGGEST;
        }

        /**
         * term suggester.
         * <p>
         * https://www.elastic.co/guide/en/elasticsearch/reference/current/search-suggesters-term.html
         *
         * @param name  string
         * @param field string
         * @return Term
         */
        public Suggest term(String name, String field) {
            return new Term(name, field);
        }

        /**
         * phrase suggester.
         * <p>
         * https://www.elastic.co/guide/en/elasticsearch/reference/current/search-suggesters-phrase.html
         *
         * @param name
         * @param field
         * @return Phrase
         */
        public Suggest phrase(String name, String field) {
            return new Phrase(name, field);
        }

        /**
         * completion suggester.
         * <p>
         * https://www.elastic.co/guide/en/elasticsearch/reference/current/search-suggesters-completion.html
         *
         * @param name
         * @param field
         * @return Completion
         */
        public Suggest completion(String name, String field) {
            return new Completion(name, field);
        }

        /**
         * context suggester.
         * <p>
         * https://www.elastic.co/guide/en/elasticsearch/reference/current/suggester-context.html
         */
        public Suggest context() {
            throw new NotImplementedException();
        }

        public Suggest setText(String text) {
            this.getRawParams().put("text", text);
            return this;
        }

        public Suggest setPrefix(String prefix) {
            this.getRawParams().put("prefix", prefix);
            return this;
        }

        public Suggest setRegex(String regex) {
            this.getRawParams().put("regex", regex);
            return this;
        }

        /**
         * Expects one of the next params: max_determinized_states - defaults to 10000,
         * flags are ALL (default), ANYSTRING, COMPLEMENT, EMPTY, INTERSECTION, INTERVAL, or NONE.
         *
         * @param value
         * @return this
         */
        public Suggest setRegexOptions(Map value) {
            this.getParams().put("regex", value);
            return this;
        }

        public Suggest setField(String field) {
            this.getParams().put("field", field);
            return this;
        }

        public Suggest setSize(int size) {
            this.getParams().put("size", size);
            return this;
        }

        /**
         * @param size maximum number of suggestions to be retrieved from each shard
         * @return this
         */
        public Suggest setShardSize(int size) {
            this.getParams().put("shard_size", size);
            return this;
        }


        /**
         * Sets the name of the suggest. It is automatically set by
         * the constructor.
         *
         * @param name The name of the suggest
         * @return this
         * @throws \Elastica\Exception\InvalidException If name is empty
         */
        public Suggest setName(String name) {
            if (StringUtils.isEmpty(name)) {
                throw new IllegalArgumentException("Suggest name has to be set");
            }
            this.name = name;

            return this;
        }
    }







