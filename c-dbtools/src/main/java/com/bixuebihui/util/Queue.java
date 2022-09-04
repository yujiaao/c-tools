package com.bixuebihui.util;

import java.util.Vector;

/**
 * <p>Queue class.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class Queue<T> {

    /**
     * <p>dequeue.</p>
     *
     * @return a {@link java.lang.Object} object.
     */
    public synchronized Object dequeue() {
        if (empty()) {
            return null;
        } else {
            Object obj = v.firstElement();
            v.removeElementAt(0);
            return obj;
        }
    }

    /**
     * <p>enqueue.</p>
     *
     * @param obj a T object.
     */
    public void enqueue(T obj) {
        v.addElement(obj);
    }

    /**
     * <p>size.</p>
     *
     * @return a int.
     */
    public int size() {
        return v.size();
    }

    /**
     * <p>empty.</p>
     *
     * @return a boolean.
     */
    public boolean empty() {
        return v.size() < 1;
    }

    /**
     * <p>Constructor for Queue.</p>
     */
    public Queue() {
        v = new Vector<T>();
    }

    /**
     * <p>Constructor for Queue.</p>
     *
     * @param i a int.
     */
    public Queue(int i) {
        v = new Vector<T>(i);
    }

    private final Vector<T> v;
}
