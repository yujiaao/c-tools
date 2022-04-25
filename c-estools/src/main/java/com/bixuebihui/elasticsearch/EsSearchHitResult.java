package com.bixuebihui.elasticsearch;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * @author xwx
 */
public class EsSearchHitResult<T> {
    String _index;
    String _type;
    List<String> sort;
    String _id;
    Double _score;
    T _source;

    public List<String> getSort() {
        return sort;
    }

    public void setSort(List<String> sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "EsSearchHitResult{" +
                "_index='" + _index + '\'' +
                ", _type='" + _type + '\'' +
                ", sort=" + sort +
                ", _id='" + _id + '\'' +
                ", _score=" + _score +
                ", _source=" + _source +
                '}';
    }

    public String get_index() {
        return _index;
    }

    public void set_index(String _index) {
        this._index = _index;
    }

    public String get_type() {
        return _type;
    }

    public void set_type(String _type) {
        this._type = _type;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Double get_score() {
        return _score;
    }

    public void set_score(Double _score) {
        this._score = _score;
    }

    public T get_source() {
        return _source;
    }

    public void set_source(T _source) {
        this._source = _source;
    }

}
