package com.bixuebihui.jdbc;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>ParamsIterator class.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class ParamsIterator implements Iterable<Object[]> {

    private final int total;

    public interface CurrentParameters {
        Object[] getCurrent(int index);
    }

    private final  CurrentParameters cur;

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Object[]> iterator() {
        return new Iterator<>() {
            final int count = total;
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < count;
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
