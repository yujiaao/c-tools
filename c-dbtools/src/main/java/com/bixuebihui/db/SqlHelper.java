package com.bixuebihui.db;

import com.bixuebihui.DbException;
import com.bixuebihui.jdbc.ISqlConditionType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.Map.Entry;

import static com.bixuebihui.db.Dialect.MYSQL;

/**
 * <p>SqlHelper class.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class SqlHelper {

	public static final String WHERE = " where ";
	public static final String HAVING = " having ";

	List<BaseFilter> filters;
	List<SqlHelper> orCond = null;
	List<SqlHelper> notCond = null;
	List<SqlHelper> andCond = null;
	private int databaseType = MYSQL;
	private boolean useNullAsCondition = false;
	private boolean acceptEmptyStringAsNullObjectInCondition = false;
	private boolean conditionTypeIsWhere = true;

	/**
	 * <p>Constructor for SqlHelper.</p>
	 *
	 * @param src a {@link SqlHelper} object.
	 */
	public SqlHelper(SqlHelper src) {
		this.filters = new ArrayList<>();
		this.filters.addAll(src.filters);
		if(src.orCond!=null) {
			this.orCond = new ArrayList<>();
			this.orCond.addAll(src.orCond);
		}
		if(src.notCond!=null) {
			this.notCond = new ArrayList<>();
			this.notCond.addAll(src.notCond);
		}
		if(src.andCond!=null){
			this.andCond = new ArrayList<>();
			this.andCond.addAll(src.andCond);
		}
		this.databaseType = src.databaseType;
		this.useNullAsCondition = src.useNullAsCondition;
		this.acceptEmptyStringAsNullObjectInCondition = src.acceptEmptyStringAsNullObjectInCondition;
	}

	/**
	 * <p>Constructor for SqlHelper.</p>
	 */
	public SqlHelper() {
		filters = new ArrayList<>();
	}
	public SqlHelper(boolean isHaving) {
		filters = new ArrayList<>();
		this.conditionTypeIsWhere = !isHaving;
	}

	public boolean isAcceptEmptyStringAsNullObjectInCondition() {
		return acceptEmptyStringAsNullObjectInCondition;
	}

	public void setAcceptEmptyStringAsNullObjectInCondition(boolean acceptEmptyStringAsNullObjectInCondition) {
		this.acceptEmptyStringAsNullObjectInCondition = acceptEmptyStringAsNullObjectInCondition;
	}

	/**
	 * <p>toCondition.</p>
	 *
	 * @return a {@link SqlPocket} object.
	 */
    public SqlPocket toCondition() {
		SqlPocket criteria = new SqlPocket();

		if ((filters == null || filters.isEmpty()) && orCond == null
				&& notCond == null && andCond == null) {
			return criteria;
		}
		if(filters!=null) {
			for (BaseFilter filter : filters) {
				buildCriteria(criteria, filter);
			}
		}

		StringBuffer condition = criteria.getCondition();
		removeLeadAndOr(condition);

		if (orCond != null) {
			if(condition.length()>0) {
				condition.insert(0, "(").append(")");
			}

			for (SqlHelper it : orCond) {
				SqlPocket cond = it.toCondition();
				condition.append(" or (")
						.append(trimWhere(cond.getCondition())).append(")");
				criteria.getParams().addAll(cond.getParams());
			}
		}
		if (notCond != null) {
			if(condition.length()>0) {
				condition.insert(0, "(").append(")");
			}

			for (SqlHelper it : notCond) {
				SqlPocket cond = it.toCondition();

				condition.append(" and not (")
						.append(trimWhere(cond.getCondition())).append(")");
				criteria.getParams().addAll(cond.getParams());
			}
		}
		if (andCond != null) {
			if(condition.length()>0) {
				condition.insert(0, "(").append(")");
			}

			for (SqlHelper it : andCond) {
				SqlPocket cond = it.toCondition();
				condition.append(" and (")
						.append(trimWhere(cond.getCondition())).append(")");
				criteria.getParams().addAll(cond.getParams());
			}
		}

		if (condition.length() > 0) {
			removeLeadAndOr(condition);
			condition.insert(0, conditionTypeIsWhere? WHERE: HAVING);
		}
		return criteria;
	}

	private void removeLeadAndOr(StringBuffer condition) {
		if (condition.length() > 0 && condition.indexOf(" and ") == 0) {
			condition.delete(0, 5);
		}
		if (condition.length() > 0 && condition.indexOf(" or ") == 0) {
			condition.delete(0, 4);
		}
	}

	/**
	 * <p>trimWhere.</p>
	 *
	 * @param trimCond a {@link java.lang.StringBuffer} object.
	 * @return a {@link java.lang.StringBuffer} object.
	 */
	protected StringBuffer trimWhere(StringBuffer trimCond) {
		if(trimCond.indexOf(conditionTypeIsWhere? WHERE : HAVING)==0) {
            trimCond.delete(0, 7);
        }
		return trimCond;
	}


	/**
	 * <p>build.</p>
	 *
	 * @return a {@link SqlPocket} object.
	 */
	public SqlPocket build() {
		return toCondition();
	}


	private boolean valueIsNotNull(Object v){
		if(v==null){
			return false;
		}
		if(!acceptEmptyStringAsNullObjectInCondition){
			return true;
		}
		return !"".equals(v);
	}
	private void buildCriteria(SqlPocket criteria, BaseFilter f)
	{
		if (valueIsNotNull(f.value) || useNullAsCondition || f instanceof NullFilter || f instanceof NotNullFilter ) {
			f.build(criteria);
		}
	}


	/**
	 * 增加
	 *
	 * @param condition 条件
	 * @param ignoreNulls 忽略null参数
	 * @return 当前实例
	 */
	public SqlHelper eq(Map<String, Object> condition, boolean ignoreNulls) {
		Set<Entry<String, Object>> e = condition.entrySet();
		for (Entry<String, Object> entry:e) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (ignoreNulls && value == null) {
                continue;
            }
			eq(key, value);
		}
		return this;
	}

	/**
	 * <p>eq.</p>
	 *
	 * @param condition a {@link java.util.Map} object.
	 * @return a {@link SqlHelper} object.
	 */
	public SqlHelper eq(Map<String, Object> condition) {
		return eq(condition, true);
	}

	/**
	 * <p>Getter for the field <code>databaseType</code>.</p>
	 *
	 * @return a int.
	 */
	public int getDatabaseType() {
		return databaseType;
	}

	/**
	 * 设置数据库类型
	 *
	 * @param databaseType
	 *            采用BaseDao里的数据类型定义
	 */
	public void setDatabaseType(int databaseType) {
		this.databaseType = databaseType;
	}

	/**
	 * <p>or.</p>
	 *
	 * @param cond a {@link SqlHelper} object.
	 * @return a {@link SqlHelper} object.
	 */
	public SqlHelper or(SqlHelper cond) {
		if (orCond == null) {
            orCond = new ArrayList<>();
        }
		this.orCond.add(cond);
		return this;
	}

	public SqlHelper and(SqlHelper cond) {
		if (andCond == null) {
			andCond = new ArrayList<>();
		}
		this.andCond.add(cond);
		return this;
	}

	/**
	 * <p>not.</p>
	 *
	 * @param cond a {@link SqlHelper} object.
	 * @return a {@link SqlHelper} object.
	 */
	public SqlHelper not(SqlHelper cond) {
		if (notCond == null) {
            notCond = new ArrayList<>();
        }
		this.notCond.add(cond);
		return this;
	}

	/**
	 * <p>in.</p>
	 *
	 * @param field a {@link java.lang.String} object.
	 * @param value a {@link java.util.Collection} object.
	 * @return a {@link SqlHelper} object.
	 */
	public SqlHelper in(String field, Collection<?> value) {
		filters.add(new InFilter(field, value));
		return this;
	}

	/**
	 * <p>in.</p>
	 *
	 * @param field a {@link java.lang.String} object.
	 * @param value a {@link SqlString} object.
	 * @return a {@link SqlHelper} object.
	 */
	public SqlHelper in(String field, SqlString value) {
		filters.add(new InFilter(field, value));
		return this;
	}


	/**
	 * <p>isNull.</p>
	 *
	 * @param field a {@link java.lang.String} object.
	 * @return a {@link SqlHelper} object.
	 */
	public SqlHelper isNull(String field) {
		filters.add(new NullFilter(field));
		return this;
	}

	public SqlHelper isNotNull(String field) {
		filters.add(new NotNullFilter(field));
		return this;
	}

	/**
	 * <p>at.</p>
	 *
	 * @param field a {@link java.lang.String} object.
	 * @param value a {@link ISqlConditionType} object.
	 * @return a {@link SqlHelper} object.
	 */
	public SqlHelper at(String field, ISqlConditionType value) {
		filters.add(new CondFilter(field, value, databaseType));
		return this;
	}

	/**
	 * <p>like.</p>
	 *
	 * @param field a {@link java.lang.String} object.
	 * @param value a {@link java.lang.String} object.
	 * @return a {@link SqlHelper} object.
	 */
	public SqlHelper like(String field, String value) {
		filters.add(new LikeFilter(field, value));
		return this;
	}

	/**
	 * <p>startWith.</p>
	 *
	 * @param field a {@link java.lang.String} object.
	 * @param value a {@link java.lang.String} object.
	 * @return a {@link SqlHelper} object.
	 */
	public SqlHelper startWith(String field, String value) {
		filters.add(new StartWithFilter(field, value));
		return this;
	}

	/**
	 * <p>eq.</p>
	 *
	 * @param field a {@link java.lang.String} object.
	 * @param value a {@link java.lang.Object} object.
	 * @return a {@link SqlHelper} object.
	 */
	public SqlHelper eq(String field, Object value) {
		filters.add(new EqualsFilter(field, value));
		return this;
	}

	/**
	 * <p>between.</p>
	 *
	 * @param field a {@link java.lang.String} object.
	 * @param leftValue a {@link java.lang.Object} object.
	 * @param rightValue a {@link java.lang.Object} object.
	 * @return a {@link SqlHelper} object.
	 */
	public SqlHelper between(String field, Object leftValue, Object rightValue) {
		filters.add(new BetweenFilter(field, leftValue, rightValue));
		return this;
	}

	/**
	 * <p> not between.</p>
	 *
	 * @param field a {@link java.lang.String} object.
	 * @param leftValue a {@link java.lang.Object} object.
	 * @param rightValue a {@link java.lang.Object} object.
	 * @return a {@link SqlHelper} object.
	 */
	public SqlHelper notBetween(String field, Object leftValue, Object rightValue) {
		filters.add(new NotBetweenFilter(field, leftValue, rightValue));
		return this;
	}


	/**
	 * <p>ne.</p>
	 *
	 * @param field a {@link java.lang.String} object.
	 * @param value a {@link java.lang.Object} object.
	 * @return a {@link SqlHelper} object.
	 */
	public SqlHelper ne(String field, Object value) {
		filters.add(new NotEqualsFilter(field, value));
		return this;
	}

	/**
	 * <p>eq.</p>
	 *
	 * @param fields an array of {@link java.lang.String} objects.
	 * @param value an array of {@link java.lang.Object} objects.
	 * @return a {@link SqlHelper} object.
	 * @throws DbException if any.
	 */
	public SqlHelper eq(String[] fields, Object[] value) {
		if (fields.length != value.length) {
            throw new DbException("fields.length must equals value.length:"
                    + StringUtils.join(fields,",") + "->" + StringUtils.join(value,","));
        }
		int i = 0;
		for (String field : fields) {
			filters.add(new EqualsFilter(field, value[i++]));
		}
		return this;
	}

	/**
	 * <p>greaterThan.</p>
	 *
	 * @param field a {@link java.lang.String} object.
	 * @param value a {@link java.lang.Object} object.
	 * @return a {@link SqlHelper} object.
	 */
	public SqlHelper greaterThan(String field, Object value) {
		filters.add(new GtFilter(field, value));
		return this;
	}

	public SqlHelper greaterOrEqualThan(String field, Object value) {
		filters.add(new GeFilter(field, value));
		return this;
	}

	/**
	 * <p>smallerThan.</p>
	 *
	 * @param field a {@link java.lang.String} object.
	 * @param value a {@link java.lang.Object} object.
	 * @return a {@link SqlHelper} object.
	 */
	public SqlHelper smallerThan(String field, Object value) {
		filters.add(new LtFilter(field, value));
		return this;
	}

	public SqlHelper smallerOrEqualThan(String field, Object value) {
		filters.add(new LeFilter(field, value));
		return this;
	}

	/**
	 * <p>isUseNullAsCondition.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isUseNullAsCondition() {
		return useNullAsCondition;
	}

	/**
	 * <p>Setter for the field <code>useNullAsCondition</code>.</p>
	 *
	 * @param useNullAsCondition a boolean.
	 */
	public void setUseNullAsCondition(boolean useNullAsCondition) {
		this.useNullAsCondition = useNullAsCondition;
	}

	/**
	 * <p>clear.</p>
	 */
	public void clear() {
		if(filters!=null) {
            this.filters.clear();
        }
		if(orCond!=null) {
            this.orCond.clear();
        }
		if(notCond!=null) {
            this.notCond.clear();
        }
	}

	/**
	 * <p>in </p>
	 *
	 * @param field a {@link java.lang.String} object.
	 * @param value an array of {@link java.lang.Object} objects.
	 * @return a {@link SqlHelper} object.
	 */
	public SqlHelper in(String field, Object[] value) {
		filters.add(new InFilter(field, Arrays.asList(value)));
		return this;
	}

	/**
	 * <p> not in</p>
	 *
	 * @param field a {@link java.lang.String} object.
	 * @param values an array of {@link java.lang.Object} objects.
	 * @return a {@link SqlHelper} object.
	 */
	public SqlHelper notIn(String field, Object[] values) {
		filters.add(new NotInFilter(field, Arrays.asList(values)));
		return this;
	}

	/**
	 * <p> not in</p>
	 *
	 * @param field a {@link java.lang.String} object.
	 * @param values an array of {@link java.lang.Object} objects.
	 * @return a {@link SqlHelper} object.
	 */
	public SqlHelper notIn(String field, Collection<?> values) {
		filters.add(new NotInFilter(field, values));
		return this;
	}

	public SqlHelper notIn(String field, SqlString sqlString) {
		filters.add(new NotInFilter(field, sqlString));
		return this;
	}


	protected abstract static class BaseFilter {
		protected final String property;
		protected final Object value;

		public BaseFilter(String property, Object value) {
			this.property = property;
			this.value = value;
		}

		/**
		 * extends input condition with current filter's condition
		 * @param pkt SqlPocket to extend
		 * @return result SqlPocket
		 */
		abstract public SqlPocket build(SqlPocket pkt) ;

	}

	protected static class NotInFilter extends InFilter {
		public NotInFilter(String property, Collection<?> value) {
			super(property, new HashSet<>(value)); //use Hashset to reduce duplicated values
			op = "not in";
		}

		public NotInFilter(String property, SqlString value) {
			super(property, value); //use Hashset to reduce duplicated values
			op = "not in";
		}

	}

	protected static class InFilter extends BaseFilter {
		String op ="in";
		public InFilter(String property, Collection<?> value) {
			super(property, new HashSet<>(value)); //use Hashset to reduce duplicated values
		}

		public InFilter(String property, SqlString value) {
			super(property, value); //use Hashset to reduce duplicated values
		}

		@Override
		public SqlPocket build(SqlPocket sb) {
			if (value instanceof SqlString) {
				sb.getCondition().append(" and ").append(property).append(" ")
						.append(op).append(" (").append(value).append(")");

			} else if (!(value instanceof Collection)) {
				throw new DbException("InFilter must have a Collection value:"
						+ value + "->" + value.getClass());
			} else {
				int size = ((Collection<?>) value).size();
				sb.addFilter(" and " +
						property + " "+op+" (" + StringUtils.repeat("?", ",", size)
						+ ")", value);
			}
			return sb;
		}
	}

	protected static abstract class AbstractBiFilter extends BaseFilter {
		String sign;

		public AbstractBiFilter(String property, Object value, String sign) {
			super(property, value);
			this.sign = sign;
		}

		@Override
		public SqlPocket build(SqlPocket sb) {
			if (value instanceof SqlString) {
				sb.getCondition().append(" and ").append(property).append(sign)
						.append(((SqlString) value).getContent());
			} else if (value != null) {
				sb.getCondition().append(" and ").append(property).append(sign)
						.append("?");
				sb.getParams().add(value);
			} else {
				sb.getCondition().append(" and ").append(property).append(sign)
						.append("null");
			}
			return sb;
		}
	}

	protected static class GtFilter extends AbstractBiFilter {
		public GtFilter(String property, Object value) {
			super(property, value, " > ");
		}
	}

	protected static class LtFilter extends AbstractBiFilter {
		public LtFilter(String property, Object value) {
			super(property, value, " < ");
		}
	}

	protected static class GeFilter extends AbstractBiFilter {
		public GeFilter(String property, Object value) {
			super(property, value, " >= ");
		}
	}

	protected static class LeFilter extends AbstractBiFilter {
		public LeFilter(String property, Object value) {
			super(property, value, " <= ");
		}
	}

	protected static class EqualsFilter extends AbstractBiFilter {
		public EqualsFilter(String property, Object value) {
			super(property, value, " = ");
		}
	}

	protected static class NotEqualsFilter extends AbstractBiFilter {
		public NotEqualsFilter(String property, Object value) {
			super(property, value, " != ");
		}
	}

	protected static class NullFilter extends AbstractBiFilter {
		public NullFilter(String property) {
			super(property, null, " is ");
		}
	}

	protected static class NotNullFilter extends AbstractBiFilter {
		public NotNullFilter(String property) {
			super(property, null, " is not ");
		}
	}
	protected static class CondFilter extends BaseFilter {
		int databaseType;

		public CondFilter(String property, Object value, int databaseType) {
			super(property, value);
			this.databaseType = databaseType;
		}

		@Override
		public SqlPocket build(SqlPocket sb) {
			if (value != null && value instanceof ISqlConditionType) {
				sb.getCondition().append(
						((ISqlConditionType) value).getConditionSql(property,
								databaseType));
			} else {
				throw new DbException(
						"CondFilter constructor only accept value of type ISqlConditionType:"
								+ value + "->" + (value == null ? "null" : value.getClass()));
			}
			return sb;
		}
	}

	protected static abstract class BaseLikeFilter extends BaseFilter {
		public BaseLikeFilter(String property, Object value){
			super(property, value);
		}

		public abstract String getConcat();

		@Override
		public SqlPocket build(SqlPocket sb) {
			if (value != null && value instanceof String) {
				sb.getCondition().append(" and ").append(property)
						.append(" like ").append(getConcat());
				sb.getParams().add(value);
			} else {
				throw new DbException(
						"CondFilter constructor only accept value of type ISqlConditionType:"
								+ value + "->" + (value == null ? "null" : value.getClass()));
			}
			return sb;
		}

	}

	protected static class LikeFilter extends BaseLikeFilter {
		public LikeFilter(String property, Object value){
			super(property, value);
		}

		@Override
		public String getConcat(){ return "concat('%',?,'%')"; }
	}

	protected static class StartWithFilter extends BaseLikeFilter {
		public StartWithFilter(String property, Object value){
			super(property, value);
		}

		@Override
		public String getConcat(){ return "concat(?,'%')";
		}

	}

	protected static class BetweenFilter extends BaseFilter {
		protected String operator = " between ";
		public BetweenFilter(String property, Object valueLeft, Object valueRight) {
			super(property, Pair.of(valueLeft, valueRight));
		}

		@Override
		public SqlPocket build(SqlPocket sb) {
			if(value instanceof Pair) {
				Pair p = (Pair) value;
				if (p.getLeft() instanceof SqlString) {
					sb.getCondition().append(" and ").append(property).append(operator).append(p.getLeft());
				} else{
					sb.getCondition().append(" and ").append(property).append(operator).append("?");
					sb.getParams().add(p.getLeft());
				}

				if (p.getRight() instanceof SqlString) {
					sb.getCondition().append(" and ").append(p.getRight());
				} else{
					sb.getCondition().append(" and ").append("?");
					sb.getParams().add(p.getRight());
				}

			} else {
				throw new DbException(
						"BetweenFilter constructor only accept value of type SqlString or Pair object:"
								+ value + "->" + (value == null ? "null" : value.getClass()));
			}
			return sb;
		}
	}

	protected static class NotBetweenFilter extends BetweenFilter {
		public NotBetweenFilter(String property, Object valueLeft, Object valueRight) {
			super(property, valueLeft, valueRight);
			operator = " not between ";
		}
	}

}
