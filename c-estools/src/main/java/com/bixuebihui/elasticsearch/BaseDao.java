package com.bixuebihui.elasticsearch;

import com.bixuebihui.db.PojoValidator;
import com.bixuebihui.jdbc.IModifiedTime;
import com.bixuebihui.jdbc.event.AfterChangeEventHandler;
import com.bixuebihui.jdbc.event.ChangeType;
import javax.validation.constraints.NotNull;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import static java.util.Collections.emptyList;

/**
 * <p>Abstract BaseDao class.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public abstract class BaseDao<T, ID> {


    /**
     * Constant <code>LOG</code>
     */
    protected static final Logger LOG = LoggerFactory.getLogger(BaseDao.class);
    protected final PojoValidator<T> pojoValidator = new PojoValidator<>();
    AfterChangeEventHandler<T, ID> afterChangeEventHandler;
    private BaseEsOperator<T, ID> operator;

    protected   String indexName;

    protected BaseDao(BaseEsOperator<T, ID> operator) {
        this.operator = operator;
    }

    public  EsActiveRecordImpl<T, ID> ar(){
        return new EsActiveRecordImpl<>(getOperator());
    }


     public ID getId(T info){
        return null;
     };

    /**
     * <p>fields.</p>
     * Comma separated column names, used for select statement when you don't want return all fields,
     * in case to optimize the index usage.
     *
     * @param fields a {@link java.lang.String} object.
     * @return an array of {@link java.lang.String} objects.
     */
    protected static String[] fields(String... fields) {
        return fields;
    }

    /**
     * <p>params.</p>
     *
     * @param params a {@link java.lang.Object} object.
     * @return an array of {@link java.lang.Object} objects.
     */
    protected static Object[] params(Object... params) {
        return params;
    }


    /**
     * 使用SimpleDateFormat需要同步才行 For more information on this see Sun Bug
     * #6231579 and Sun Bug #6178997.
     *
     * @param dt 日期
     * @return 格式化的日期字符串
     */
    private static String formatDate(Date dt) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        return fmt.format(dt);
    }

    /**
     * <p>registerConverters.</p>
     *
     * @param convertors a {@link java.util.Map} object.
     */
    public static void registerConverters(Map<Class, Converter> convertors) {
        BeanUtilsBean b = BeanUtilsBean.getInstance();
        for (Entry<Class, Converter> entry : convertors.entrySet()) {
            b.getConvertUtils().register(entry.getValue(), entry.getKey());
        }
    }

    /**
     * contain field check
     *
     * @param field
     * @param fields
     * @return true if fields is empty or contains field, ignore
     */
    protected static boolean containsIgnoreCase(String field, Set<String> fields) {
        return fields == null || fields.isEmpty() || fields.contains(getFieldAlias(field).toLowerCase(Locale.ROOT));
    }

    /**
     * deal with alias and `` quotes
     *
     * @param field
     * @return
     */
    public static String getFieldAlias(String field) {
        field = field.strip();

        if (!field.startsWith("`") && StringUtils.containsWhitespace(field)) {
            StringTokenizer tokenizer = new StringTokenizer(field);
            while (tokenizer.hasMoreTokens()) {
                field = tokenizer.nextToken();
            }
        } else if (field.startsWith("`")) {
            field = StringUtils.strip("`");
        }
        return field;
    }

    public BaseEsOperator<T, ID> getOperator() {
        return operator;
    }

    public AfterChangeEventHandler<T, ID> getAfterChangeEventHandler() {
        return afterChangeEventHandler;
    }

    public void setAfterChangeEventHandler(AfterChangeEventHandler<T, ID> afterChangeEventHandler) {
        this.afterChangeEventHandler = afterChangeEventHandler;
    }

    public String getIndexName(){
        return  indexName;
    }

    public List<String> getAllFields() {
        return emptyList();
    }

    /**
     * <p>beforeChange.</p>
     *
     * @param info a T object.
     */
    protected void beforeChange(T info) {
        pojoValidator.asureValid(info);
        if (info instanceof IModifiedTime) {
            ((IModifiedTime) info).setModifiedTime(new Timestamp(
                    System.currentTimeMillis()
            ));
        }
    }

    private void beforeChange(T info, long time) {
        pojoValidator.asureValid(info);
        if (info instanceof IModifiedTime) {
            ((IModifiedTime) info).setModifiedTime(new Timestamp(
                    time
            ));
        }
    }

    private void beforeChange(T[] infos) {
        long time = System.currentTimeMillis();
        for (T info : infos) {
            beforeChange(info, time);
        }
    }


    /**
     * <p>getKeyName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getKeyName(){
        return null;
    }




    private void afterUpdate(String query, Object[] params,  int res) {
        if(res >0 && afterChangeEventHandler!=null){
            afterChangeEventHandler.afterChange(query, params, ChangeType.UPDATE, res);
        }
    }

    private void afterUpdate(T[] infos, boolean res) {
        if(res && afterChangeEventHandler!=null){
            afterChangeEventHandler.afterChange(infos, ChangeType.UPDATE);
        }
    }

    private void afterUpdate(T infos, boolean res) {
        if(res && afterChangeEventHandler!=null){
            afterChangeEventHandler.afterChange(infos, ChangeType.UPDATE);
        }
    }


    /**
     * <p>convertCaseInsensitive.</p>
     *
     * @param <K>      期待类型
     * @param h        源数据
     * @param receiver 目标对象
     * @return 目标对象
     * @throws java.lang.IllegalAccessException            无法拷贝属性
     * @throws java.lang.reflect.InvocationTargetException 无法拷贝属性
     * @throws java.lang.NoSuchMethodException             无法拷贝属性
     */
    protected @NotNull <K> K convertCaseSensitive(Map<String, Object> h, @NotNull K receiver)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        // access properties as Map
        Map<String, String> properties = BeanUtils.describe(receiver);
        Map<String, Object> values = new HashMap<>(h.size() * 2);
        for (Map.Entry<String, ?> e : properties.entrySet()) {
            Object o = h.get(e.getKey());
            values.put(e.getKey(), o);
        }
        BeanUtils.populate(receiver, values);
        return receiver;
    }



    private void afterDelete(ID id, boolean res) {
        if(res && afterChangeEventHandler!=null){
            afterChangeEventHandler.afterChangeByKey(id, ChangeType.DELETE);
        }
    }
    private void afterDelete(ID[] id, boolean res) {
        if(res && afterChangeEventHandler!=null){
            afterChangeEventHandler.afterChangeByKey(id, ChangeType.DELETE);
        }
    }



    /**
     * <p>makeInPlaceHolder.</p>
     *
     * @param fieldName a {@link java.lang.String} object.
     * @param size      a int.
     * @return a {@link java.lang.String} object.
     */
    protected String makeInPlaceHolder(String fieldName, int size) {
        return fieldName + " in (" + StringUtils.repeat("?", ",", size) + ")";
    }



    String shardNameStrategy(String sql, String shardTableName) {
        return sql.replaceAll(getIndexName(), shardTableName);
    }

    /**
     * Inserts the info object values into the database.
     *
     * @param info pojo
     * @return true if success
     */
    public boolean save(T info) {
        return save(info, false);
    }

    public boolean save(T info, boolean refresh) {
        beforeChange(info);

        boolean res = ar().insert(getIndexName(),info, (t) -> {
            ID v = getId(t);
            return v==null?null: v.toString();
        }, refresh);

        afterInsert(info, res);
        return res;
    }

    private void afterInsert(T info, boolean res) {
        if(res && afterChangeEventHandler!=null){
            afterChangeEventHandler.afterChange(info, ChangeType.INSERT);
        }
    }

    private void afterInsert(T[] info, boolean res) {
        if(res && afterChangeEventHandler!=null){
            afterChangeEventHandler.afterChange(info, ChangeType.INSERT);
        }
    }

    private void afterInsert(T info, boolean res, String shardTableName) {
        if(res && afterChangeEventHandler!=null){
            afterChangeEventHandler.afterChange(info, ChangeType.INSERT, shardTableName);
        }
    }

    public boolean save(T info, String tableShardName) {
        return save(info, tableShardName, false);
    }
    public boolean save(T info, String tableShardName, boolean refresh) {
        beforeChange(info);
        boolean res =  ar().insert(tableShardName, info);

        afterInsert(info, res);
        return res;
    }

    /**
     * <p>insertBatch.</p>
     *
     * @param infos an array of T[] objects.
     * @return a boolean.
     */
    public boolean saveBatch(List<T> infos) {
        return saveBatch(infos, false);
    }
    public boolean saveBatch(List<T> infos, boolean refresh) {

        boolean res=  ar().insertBatch(getIndexName(), infos, (t) ->{
            ID v  = getId(t);
            return v != null ? v.toString() : null;
        }, refresh);

        return res;
    }

    public boolean updateBatch(List<T> infos, boolean refresh) {
        return ar().updateBatch(getIndexName(), infos, (t) ->{
            ID v  = getId(t);
            return v != null ? v.toString() : null;
        }, refresh);
    }

    public boolean updateBatch(List<T> infos, boolean refresh, Class<?> mixInClass) {
        return ar().updateBatch(getIndexName(), infos, (t) ->{
            ID v  = getId(t);
            return v != null ? v.toString() : null;
        }, refresh, mixInClass, true);
    }

    /**
     * <p>getIDs.</p>
     *
     * @param infos a {@link java.util.List} object.
     * @return a {@link java.util.List} object.
     */
    public @NotNull
    List<ID> getIDs(List<T> infos) {
        List<ID> res = new ArrayList<>();
        for (T info : infos) {
            res.add(getId(info));
        }
        return res;
    }


}
