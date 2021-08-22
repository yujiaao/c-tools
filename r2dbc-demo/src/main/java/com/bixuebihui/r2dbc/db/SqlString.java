package com.bixuebihui.r2dbc.db;
/**
 * 这个用于产生原生sql,
 * 使用场景类似于 update table1 set a = a+1 中的a+1,
 * 避免被预编译语句占位符替换
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class SqlString {
	private String content;

    /**
     * <p>Constructor for SqlString.</p>
     *
     * @param content a {@link String} object.
     */
    public SqlString(String content){
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
