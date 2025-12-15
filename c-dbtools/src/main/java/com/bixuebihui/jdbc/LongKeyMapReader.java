package com.bixuebihui.jdbc;

import org.apache.commons.lang3.tuple.Pair;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class LongKeyMapReader<K, T> implements RowCallbackHandler {
    private final List<Pair<K, T>> list;
    private final Class<T> valueClass;
    private final Class<K> keyClass;

    public LongKeyMapReader(List<Pair<K, T>> list, Class<K> keyClass, Class<T> valueClass) {
        this.list = list;
        this.keyClass = keyClass;
        this.valueClass = valueClass;
    }

    @Override
    public void processRow(ResultSet rs) throws SQLException {
        Object key = rs.getObject(1);
        K keyValue = toClazz(key, keyClass);
        Object obj = rs.getObject(2);
        T value = toClazz(obj, valueClass);
        if (!rs.wasNull()) {
            list.add(Pair.of(keyValue, value));
        }
    }

    private static <T> T toClazz(Object obj, Class<T> clazz) {
        T value;
        if (obj == null) {
            value = null;
        } else if (clazz.isInstance(obj)) {
            value = clazz.cast(obj);
        } else if (clazz == String.class) {
            value = clazz.cast(obj.toString());
        } else if (clazz == Integer.class) {
            value = clazz.cast(((Number) obj).intValue());
        } else if (clazz == Long.class) {
            value = clazz.cast(((Number) obj).longValue());
        } else if (clazz == Double.class) {
            value = clazz.cast(((Number) obj).doubleValue());
        } else if (clazz == Float.class) {
            value = clazz.cast(((Number) obj).floatValue());
        } else if (clazz == Short.class) {
            value = clazz.cast(((Number) obj).shortValue());
        } else if (clazz == Boolean.class) {
            value = clazz.cast(Boolean.valueOf(obj.toString()));
        } else {
            value = clazz.cast(obj);
        }
        return value;
    }
}