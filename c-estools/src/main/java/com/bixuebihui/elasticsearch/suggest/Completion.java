package com.bixuebihui.elasticsearch.suggest;

import java.util.ArrayList;

/**
 * @author xwx
 */
public class Completion extends Suggest {
        public Completion(String name, String field) {
            super(name, field);
        }

        public Completion setFuzzy(ArrayList<?> fuzzy) {
            this.getParams().put("fuzzy", fuzzy);
            return this;
        }

    }
