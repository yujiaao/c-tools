package com.bixuebihui.tablegen.generator;


import com.bixuebihui.db.Dialect;
import com.bixuebihui.dbcon.DatabaseConfig;
import com.bixuebihui.jdbc.IDbHelper;
import com.bixuebihui.tablegen.*;
import com.bixuebihui.tablegen.entry.ColumnData;
import com.bixuebihui.tablegen.entry.TableInfo;
import com.bixuebihui.tablegen.entry.TableSetInfo;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.context.MethodValueResolver;
import com.github.jknack.handlebars.helper.ConditionalHelpers;
import com.github.jknack.handlebars.helper.StringHelpers;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static com.bixuebihui.tablegen.NameUtils.firstUp;
import static com.bixuebihui.tablegen.NameUtils.getConfigBaseDir;
import static com.bixuebihui.tablegen.TableGenConfig.PROPERTIES_FILENAME;

/**
 * @author xwx
 */
public abstract class BaseGenerator implements Generator {
    public final static String UNKNOWN_TYPE = "unknown";
    protected static final String TEMPLATE_ROOT = "/templates";
    public static final String VIEW_DIR = "view";
    protected static final Logger LOG = LoggerFactory.getLogger(DalGenerator.class);


    ProjectConfig config;
    DatabaseConfig dbConfig;
    TableSetInfo setInfo;

    Properties esConfig;

    int dbType =0;

    public int getDbType(){
        return dbType;
    }

    protected boolean isView(){
        return this instanceof IView;
    }

    public BaseGenerator() {
        config = new ProjectConfig();
        dbConfig = new DatabaseConfig();
        setInfo = new TableSetInfo();
    }

    public static boolean isNotEmpty(Collection<?> col) {
        return !CollectionUtils.isEmpty(col);
    }

    /**
     * Selects the type of a particular column name. Cannot use Hashtables to
     * store columns as it screws up the ordering, so we have to do a crap
     * search. (and yes I know it could be better - it's good enough).
     */
    public static String getColType(String key, List<ColumnData> columnData) {
        String type = UNKNOWN_TYPE;
        for (ColumnData tmp : columnData) {
            if (tmp.getName().equalsIgnoreCase(key)) {
                type = tmp.getJavaType();
                break;
            }
        }
        if (UNKNOWN_TYPE.equals(type)) {
            LOG.error("unknown type of key:{}", key);
        }
        return type;
    }

    public static String getPrimaryKeyType(List<String> keyData2, List<ColumnData> columnData) throws GenException {
        return (isNotEmpty(keyData2))
                ? (keyData2.size()>1
                    ? "List<Object>"
                    : BaseGenerator.getColType(keyData2.get(0), columnData))
                : "Object";
    }

    public static String getPrimaryKeyFirstColumnType(List<String> keyData2, List<ColumnData> columnData) throws GenException {
        return isNotEmpty(keyData2)?
              BaseGenerator.getColType(keyData2.get(0), columnData)
                : "Object";
    }


    /**
     * to reuse previews created config objects
     * @param config
     * @param dbConfig
     * @param setInfo
     */
    @Override
    public void init(ProjectConfig config, DatabaseConfig dbConfig, TableSetInfo setInfo, Properties prop) {
        this.config = config;
        this.dbConfig = dbConfig;
        this.setInfo = setInfo;
        this.esConfig = prop;
        this.dbType = Dialect.detectDbType(dbConfig.getDburl());
    }

    protected List<ColumnData> getColsInfo(String tableName) {
        List<ColumnData> cols = isView() ? this.setInfo.getViewCols(tableName) :
                this.setInfo.getTableCols(tableName);

        return cols;
    }

    protected List<String> getKeysInfo(List<ColumnData> cols, String tableName){
        List<String> keys = getKeyData(tableName);
        if(keys.isEmpty() && isView()){
            keys = new ArrayList<>();
            keys.add(cols.get(0).getName());
        }
        return keys;
    }

    String getClassSuffix(){return "";}

    protected String getSourceDir(){
        return config.getBaseSrcDir();
    }

    protected String getBaseDir() {
        String baseDir = getSourceDir();
        //相对路径则相对于配置文件所在目录，避免windows 与linux 的差异
        if(!baseDir.startsWith("/") && !baseDir.contains(":")){
            baseDir = config.getBaseDir() + baseDir;
        }
        return baseDir;
    }


    String getTargetFileName(String subDir, String tableName) {
        return
                getBaseDir() + (isView() ?  File.separator + VIEW_DIR : "")+ File.separator + subDir + File.separator + getPojoClassName(tableName)
                        + getClassSuffix() + ".java";
    }

    public synchronized void readDbMetaData(DatabaseConfig dbConfig) throws SQLException, InstantiationException, IOException, IllegalAccessException {
        IDbHelper helper = TableGen.getDbHelper(dbConfig);
        try(Connection cn = helper.getConnection()) {
            DatabaseMetaData metaData = cn.getMetaData();
            setInfo.getTableData(config, helper, metaData);
            setInfo.getOnFlyViewData(config, helper, metaData);
        }
    }

    /**
     * name of file to generate
     * @param tableName database table name
     * @return full path of file name
     */
    @Override
    public abstract String getTargetFileName(String tableName);

    /**
     * template file name
     * @return template file name
     */
    abstract String getTemplateFileName(String tableName);


    public static String getPojoClassName(String tableName, ProjectConfig config, TableSetInfo setInfo) {
        String classname = setInfo.tableName2ClassName(tableName);
        String prefix = config.getAddPrefix() == null ? "" : config.getAddPrefix();
        if (tableName.equals(classname)) {
            return prefix + firstUp(removePrefix(tableName, config.getRemovePrefix()));
        } else {
            return prefix + classname;
        }
    }


    public String getPojoClassName(String tableName) {
        return getPojoClassName(tableName, config, setInfo);
    }

    protected static String removePrefix(String tableName, String removePrefix){
       return StringUtils.removeStart(tableName, removePrefix);
    }

    /**
     * Java class name to generate
     * @param tableName
     * @return
     */
    abstract String getClassName(String tableName);

    /**
     * read config from file
     * @param filename the configuration file name
     */
    public synchronized boolean init(String filename) {
        String propertiesFilename = filename != null ? filename : PROPERTIES_FILENAME;

        try (FileInputStream fis = new FileInputStream(propertiesFilename)) {
            Properties props = new Properties();

            props.load(fis);

            config = ProjectConfig.readFrom(props, getConfigBaseDir(propertiesFilename));

            if(props.getProperty("url")!=null){
                esConfig = props;
                return true;
            }

            dbConfig = DatabaseConfig.newInstance(props);
            readDbMetaData(dbConfig);

        } catch (IOException | SQLException | InstantiationException | IllegalAccessException e) {
            LOG.error(e.getMessage(), e);
        }
        return false;
    }

    protected String getInterface(String tableName) {
        return setInfo.getInterface(tableName, config);
    }

    protected String getExtendsClasses(String tableName) {
        return "";
    }

    protected void additionalSetting(Handlebars handlebars) {
    }

    @Override
    public String generate(String tableName, int index) throws IOException {
        Handlebars handlebars = getHandlebars();

        Template template = handlebars.compile(getTemplateFileName(tableName));

        Map<String, Object> v = getContextMap(tableName);
        return template.apply(Context.newBuilder(v).resolver(
                MethodValueResolver.INSTANCE,
                JavaBeanValueResolver.INSTANCE,
                FieldValueResolver.INSTANCE,
                MapValueResolver.INSTANCE
        ).build());
    }

    protected Map<String, Object> getContextMap(String tableName) {
        Map<String, Object> v = new HashMap<>(20);
        v.put("tableName", tableName);

        if(esConfig!=null) {
            for (Map.Entry<Object, Object> entry : esConfig.entrySet()) {
                if (entry.getKey() instanceof String) {
                    v.put((String) entry.getKey(), entry.getValue());
                }
            }

        }else{
            LinkedHashMap<String, TableInfo> tableInfos = setInfo.getTableInfos();
            if (tableInfos!=null && tableInfos.get(tableName) != null) {
                v.put("tableInfo", tableInfos.get(tableName));

                v.put("fields", getFieldsList(tableName));;
                v.put("keys", setInfo.getTableKeys(tableName));
                v.put("importKeys", setInfo.getTableImportedKeys(tableName));
                v.put("exportKeys", setInfo.getTableExportedKeys(tableName));
                v.put("indexData", setInfo.getTableIndexes(tableName));
            }
            v.put("hasKey", isNotEmpty(this.setInfo.getTableKeys(tableName)));
        }

        v.put("pojoClassName", this.getPojoClassName(tableName));
        v.put("className", this.getClassName(tableName));
        v.put("interface", this.getInterface(tableName));
        v.put("extends", this.getExtendsClasses(tableName));
        v.put("setInfo", setInfo);
        v.put("config", config);



        if(isView()) {
            setViewContext(v, tableName);
        }
        return v;
    }

    protected List<ColumnData> getFieldsList(String tableName) {
        return subList(setInfo.getTableCols(tableName), config.getIgnoreFields().get(tableName));
    }

    private List<ColumnData> subList(List<ColumnData> tableCols, List<String> strings) {
        //过滤掉不需要的字段
        if(!CollectionUtils.isEmpty(strings)){
            Set<String> set = new HashSet<>(strings);
            return tableCols.stream().filter(col->!set.contains(col.getName())).collect(Collectors.toList());
        }
        return tableCols;
    }


    protected void setViewContext(Map<String, Object> map, String tableName) {
        map.put("tableInfo", setInfo.getViewInfos().get(tableName));
        map.put("fields", subList(setInfo.getViewInfos().get(tableName).getFields(), config.getIgnoreFields().get(tableName)));
        map.put("viewModifier","."+ VIEW_DIR);
    }


    public boolean isInsertAndUpdatableField(String tableName, String fieldName){
        // int type = BaseDao.detectDbType(this.dbConfig.getDburl());
        List<ColumnData> cols = setInfo.getTableCols(tableName);

        return isInsertAndUpdatableField(cols, fieldName, config.isUseAutoincrement());
    }
    public static boolean isInsertAndUpdatableField(List<ColumnData> cols, String fieldName, boolean useAutoIncrement){
        for(ColumnData col : cols){
            if(col.getName().equals(fieldName) && isInsertAndUpdatableField(col, useAutoIncrement)){
                return false;
            }
        }
        return true;
    }

    public static boolean isInsertAndUpdatableField(ColumnData col, boolean useAutoIncrement){
        // - 18 sql server row-version
        return  col.getType()!= 18 && !( col.isAutoIncrement() && useAutoIncrement);
    }

    protected Handlebars getHandlebars() {
        TemplateLoader loader = new ClassPathTemplateLoader();
        loader.setPrefix(getTemplateDir());
        loader.setSuffix(".hbs");

        Handlebars handlebars = new Handlebars(loader);

        handlebars.registerHelpers(ConditionalHelpers.class);
        handlebars.registerHelpers(StringHelpers.class);
        handlebars.registerHelpers(new HelperSource(config, setInfo));

        additionalSetting(handlebars);
        return handlebars;
    }

    private String getTemplateDir() {
        return TEMPLATE_ROOT+"/"+  (esConfig==null?
                (config.getR2dc() ? "r2dbc" : "jdbc")
                :"es");
    }

    String getPrimaryKeyType(String tableName) {
        try {
            List<String> keyData = getKeyData(tableName);
            List<ColumnData> columnData = getColumnData(tableName);
            return BaseGenerator.getPrimaryKeyType(keyData, columnData);
        } catch (GenException e) {
            LOG.error(e.getMessage(),e);
        }
        return "";
    }

    String getPrimaryKeyFirstColumnType(String tableName) {
        try {
            List<String> keyData = getKeyData(tableName);
            List<ColumnData> columnData = getColumnData(tableName);
            return BaseGenerator.getPrimaryKeyFirstColumnType(keyData, columnData);
        } catch (GenException e) {
            LOG.error(e.getMessage(),e);
        }
        return "";
    }

    private List<ColumnData> getColumnData(String tableName) {
        return isView() ? setInfo.getViewInfos().get(tableName).getFields() :
                setInfo.getTableInfos().get(tableName).getFields();
    }

    protected List<String> getKeyData(String tableName) {
        List<String> keys =  setInfo.getTableKeys(tableName);
        if(keys.isEmpty() ){
            List<ColumnData> cols = getColumnData(tableName);
            keys = new ArrayList<>();
            if(isView()) {
                keys.add(cols.get(0).getName());
            }else{
                for (ColumnData column: cols){
                    if(column.isAutoIncrement()){
                        keys.add(column.getName());
                        break;
                    }
                }
            }
        }
        return keys;
    }


    public static String getPrimaryKeyName(List<String> keyData) {
        if (isNotEmpty(keyData)) {
            return String.join(",", keyData);
        }
        return null;
    }

    public static String getPrimaryKeyFirstColumnName(List<String> keyData) {
        if (isNotEmpty(keyData)) {
            return keyData.get(0);
        }
        return null;
    }


    public static boolean containsColName(List<ColumnData> cols, String colName) {
        return TableUtils.isExistsCol(cols, colName);
    }

    protected List<String> getKeyNames(String tableName) {
        List<String> keyData = getKeyData(tableName);
        if (!isNotEmpty(keyData)) {
            LOG.warn("Table '{}' have not a  primary key, this may be a error", tableName);
        }
        return keyData;
    }

    protected String getFirstKeyFirstColumnName(String tableName) {
        List<String> keyData = getKeyNames(tableName);
        return getPrimaryKeyFirstColumnName(keyData);
    }


    protected String getFirstKeyName(String tableName) {
        List<String> keyData = getKeyNames(tableName);
        return getPrimaryKeyName(keyData);
    }


}
