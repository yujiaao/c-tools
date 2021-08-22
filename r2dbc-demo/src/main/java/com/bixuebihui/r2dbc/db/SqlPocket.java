package com.bixuebihui.r2dbc.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>SqlPocket class.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class SqlPocket {
	private StringBuffer condition;
	private List<Object> params;

	/**
	 * <p>Constructor for SqlPocket.</p>
	 */
	public SqlPocket() {
		condition = new StringBuffer();
		params = new ArrayList<>();
	}

	/**
	 * <p>addFilter.</p>
	 *
	 * @param fragment a {@link String} object.
	 * @param param a {@link Object} object.
	 * @return a {@link SqlPocket} object.
	 */
	public SqlPocket addFilter(String fragment, Object param) {
		condition.append(fragment);
		if (param instanceof Collection) {
			params.addAll((Collection<?>) param);
		} else {
            params.add(param);
        }
		return this;
	}

	/**
	 * <p>Getter for the field <code>condition</code>.</p>
	 *
	 * @return a {@link StringBuffer} object.
	 */
	public StringBuffer getCondition() {
		return condition;
	}

	/**
	 * <p>Getter for the field <code>params</code>.</p>
	 *
	 * @return a {@link List} object.
	 */
	public List<Object> getParams() {
		return params;
	}
}
