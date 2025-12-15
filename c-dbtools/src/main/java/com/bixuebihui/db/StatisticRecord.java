package com.bixuebihui.db;

import com.bixuebihui.DbException;

/**
 * <p>Record interface.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public interface StatisticRecord<T> {

	/**
	 * <p>percentile </p>
	 *
	 *
	 * @param field a {@link java.lang.String} object.
	 * @param portion a float between 0~1.
	 * @return a int.
	 * @throws DbException if any.
	 */
	Object percentile(String field, float portion)throws DbException;

}
