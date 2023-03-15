package com.bixuebihui;

import java.io.Serializable;
import java.util.List;

/**
 * @author xwx
 */
public class PageModel<T> implements Serializable {
    private long total;
    private List<T> items;
    /**
     * 执行时长
     */
    private long took;

    private PageModel() {
    }

    public static <T> Builder<T> builder(List<T> data) {
        return new Builder().data(data);
    }


    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    /**
     * 获取 执行时长
     */
    public long getTook() {
        return this.took;
    }

    /**
     * 设置 执行时长
     */
    public void setTook(long took) {
        this.took = took;
    }


    public static class Builder<T> {
        private List<T> data;
        private long total;
        private int took;

        private Builder() {
            this.total = 0;
        }

        public Builder<T> total(long total) {
            this.total = total;
            return this;
        }

        public Builder<T> took(int took) {
            this.took = took;
            return this;
        }

        public Builder<T> data(List<T> data) {
            this.data = data;
            return this;
        }

        public PageModel<T> build() {
            PageModel<T> pager = new PageModel<T>();
            pager.setTotal(total);
            pager.setItems(data);
            pager.setTook(took);
            return pager;
        }
    }
}
