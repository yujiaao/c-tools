/**
 *
 */
package com.bixuebihui.jdbc;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>SqlSort class.</p>
 *
 * @author xwx
 * @version $Id: $Id
 */
public class SqlSort {

	protected List<Sortable> sorts = new ArrayList<>();

    /**
     * <p>addSort.</p>
     *
     * @param property a {@link java.lang.String} object.
     * @param order    a {@link java.lang.String} object.
     */
    public void addSort(String property, String order) {
        sorts.add(new Sort(property, order));
    }

    public void addSortByIn(String property, Object[] order) {
        sorts.add(new SortByIn(property, order));
    }

    /**
     * <p>Constructor for SqlSort.</p>
     *
     * @param src a {@link SqlSort} object.
     */
    public SqlSort(SqlSort src) {
        //Sort为不变类，多个类引用不会因意外改变造不可预知问题
        this.sorts.addAll(src.sorts);
    }

    /**
     * <p>Constructor for SqlSort.</p>
     */
    public SqlSort() {
    }


    /**
     * <p>toString.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Override
    public String toString() {
        if (sorts.size() <= 0) {
            return "";
        }

        StringBuilder criteria = new StringBuilder(" order by ");
        for (Sortable sort : sorts) {
            if(sort instanceof Sort) {
                buildCriteria(criteria, ((Sort)sort).getProperty(), ((Sort)sort).getOrder());
            }else if(sort instanceof SortByIn){
                buildCriteria(criteria, ((SortByIn)sort).getProperty(), ((SortByIn)sort).getIds());
            }
        }

        if (criteria.lastIndexOf(",") == criteria.length() - 1) {
            return criteria.substring(0, criteria.length() - 1);
        }
        return criteria.toString();
    }

	private void buildCriteria(StringBuilder criteria, String property,
			String order) {
		if (order.equals(Sort.ASC)) {
			criteria.append(property).append(" ").append(Sort.ASC).append(",");
		} else if (order.equals(Sort.DESC)) {
			criteria.append(property).append(" ").append(Sort.DESC).append(",");
		}
	}
    private void buildCriteria(StringBuilder criteria, String property,
                               Object[] order) {
        if(order ==null || order.length==0){return;}

        criteria.append(" field( "+property+","+ StringUtils.repeat("?",",", order.length)+")");
    }

    public  List<Object> getParams(){
        List<Object> res = new ArrayList<>();
        for(Sortable sort: sorts){
            if(sort instanceof SortByIn){
                res.addAll(Arrays.asList(((SortByIn)sort).getIds()));
            }
        }
        return res;
    }


	public interface Sortable{}
    /**
     * Sort 为不变类， 便于克隆
     */
    public static class Sort implements Sortable{


        public final static String ASC = "asc";
        public final static String DESC = "desc";

        private final String property;
        private final String order;

        public Sort(String property, String order) {

            assert order==null || order.isEmpty() || ASC.equals(order) || DESC.equals(order) || "ascend".equals(order) || "descend".equals(order);
            
            this.property = property;
            if("ascend".equalsIgnoreCase(order)){
                this.order = ASC;
            }else if("descend".equalsIgnoreCase(order)){
                this.order = DESC;
            }else {
                this.order = order;
            }
        }

        public String getProperty() {
            return property;
        }

        public String getOrder() {
            return order;
        }

        @Override
        public String toString() {
            return "{" +
                    "property='" + property + '\'' +
                    ", order='" + order + '\'' +
                    '}';
        }
    }

    public static class SortByIn implements Sortable{
        private final String property;
        private final Object[] order;

        public SortByIn(String property, Object[] ids) {
            this.property = property;
            this.order = ids;
        }

        public String getProperty() {
            return property;
        }

        public Object[] getIds() {
            return this.order;
        }
    }

    /**
     * <p>clear.</p>
     */
    public void clear() {
        if(sorts!=null) {
            this.sorts.clear();
        }
    }

}
