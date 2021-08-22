package com.bixuebihui.r2dbc.sql;

/**
 * <p>SqlObject class.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class SqlObject {
	private String sqlString;
	private Object[] parameters;
	private int expectedResult;

	/**
	 * <p>Getter for the field <code>sqlString</code>.</p>
	 *
	 * @return a {@link String} object.
	 */
	public String getSqlString() {
		return sqlString;
	}

	/**
	 * <p>Setter for the field <code>sqlString</code>.</p>
	 *
	 * @param sqlString a {@link String} object.
	 */
	public void setSqlString(String sqlString) {
		this.sqlString = sqlString;
	}

	/**
	 * <p>Getter for the field <code>parameters</code>.</p>
	 *
	 * @return an array of {@link Object} objects.
	 */
	public Object[] getParameters() {
		return parameters;
	}

	/**
	 * <p>Setter for the field <code>parameters</code>.</p>
	 *
	 * @param parameters an array of {@link Object} objects.
	 */
	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

	//set to -1 to ignored result

	/**
	 * <p>Getter for the field <code>expectedResult</code>.</p>
	 *
	 * @return a int.
	 */
	public int getExpectedResult() {
		return expectedResult;
	}

	/**
	 * <p>Setter for the field <code>expectedResult</code>.</p>
	 *
	 * @param expectedResult a int.
	 */
	public void setExpectedResult(int expectedResult) {
		this.expectedResult = expectedResult;
	}

}
