package com.bixuebihui.r2dbc.sql;


/**
 * 用于支持Oracle的CLOB字段
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class ClobString {
	private String content;

    /**
     * <p>Constructor for ClobString.</p>
     *
     * @param content a {@link String} object.
     */
    public ClobString(String content){
        this.content = content;
    }

    /**
     * <p>Getter for the field <code>content</code>.</p>
     *
     * @return a {@link String} object.
     */
    public String getContent() {
        return content;
    }

    /**
     * <p>Setter for the field <code>content</code>.</p>
     *
     * @param content a {@link String} object.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * <p>toString.</p>
     *
     * @return a {@link String} object.
     */
    @Override
    public String toString() {
        return content;
    }
}
