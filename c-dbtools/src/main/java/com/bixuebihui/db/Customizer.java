package com.bixuebihui.db;

import com.bixuebihui.jdbc.SqlGroup;
import com.bixuebihui.jdbc.SqlSort;

import java.util.List;
import java.util.function.Function;

public interface Customizer<T> {
    ActiveRecord<T> filterInterceptor(Function<SqlHelper, SqlHelper> filterInterceptor);
    ActiveRecord<T> havingInterceptor(Function<SqlHelper, SqlHelper> havingInterceptor);
    ActiveRecord<T> sortInterceptor(Function<SqlSort, SqlSort> sortInterceptor);
    ActiveRecord<T>  groupInterceptor(Function<SqlGroup, SqlGroup> groupInterceptor);
    ActiveRecord<T>  paramsInterceptor(Function<List<Object>, List<Object>> groupInterceptor);
}
