package com.bixuebihui.elasticsearch;

import com.bixuebihui.elasticsearch.processor.Sort;
import com.bixuebihui.jdbc.SqlSort;

import java.util.LinkedHashMap;

/**
 * @author xwx
 */
public class EsSort extends SqlSort {

    LinkedHashMap<String,String>  build(){
        LinkedHashMap<String,String> res = new LinkedHashMap<>();

        for (SqlSort.Sortable sort : this.sorts) {
            if(sort instanceof Sort) {
                res.put(((Sort)sort).getProperty(), ((Sort)sort).getOrder());
            }
        }

        return res;
    }

}
