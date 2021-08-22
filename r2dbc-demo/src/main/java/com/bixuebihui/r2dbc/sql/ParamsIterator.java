package com.bixuebihui.r2dbc.sql;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>ParamsIterator class.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class ParamsIterator implements Iterable<Object[]> {

    public interface CurrentParameters {
        Object[] getCurrent(int index);
    }

    int total = 0;
    int index = 0;
    CurrentParameters cur;

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Object[]> iterator() {
        return new Iterator<Object[]>() {
            @Override
            public boolean hasNext() {
                return index < total;
            }

            @Override
            public Object[] next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return cur.getCurrent(index++);
            }
        };
    }

    /**
     * <p>Constructor for ParamsIterator.</p>
     *
     * @param total a int.
     * @param cur a {@link ParamsIterator.CurrentParameters} object.
     */
    public ParamsIterator(int total, CurrentParameters cur) {
        this.total = total;
        this.cur = cur;
    }


}
