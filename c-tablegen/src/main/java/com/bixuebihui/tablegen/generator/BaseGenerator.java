package com.bixuebihui.tablegen.generator;


import com.bixuebihui.dbcon.DatabaseConfig;
import com.bixuebihui.jdbc.IDbHelper;
import com.bixuebihui.tablegen.GenException;
import com.bixuebihui.tablegen.NameUtils;
import com.bixuebihui.tablegen.ProjectConfig;
import com.bixuebihui.tablegen.TableGen;
import com.bixuebihui.tablegen.entry.ColumnData;
import com.bixuebihui.tablegen.entry.TableSetInfo;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.context.MethodValueResolver;
import com.github.jknack.handlebars.helper.ConditionalHelpers;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.*;

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

    protected boolean isView = false;

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
            LOG.error("unknown type of key:" + key);
        }
        return type;
    }

    public static String getFirstKeyType(List<String> keyData2, List<ColumnData> columnData) throws GenException {
        return (isNotEmpty(keyData2)) ? BaseGenerator.getColType(keyData2.get(0), columnData) : "Object";
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
                getBaseDir() + File.separator + subDir + File.separator + getPojoClassName(tableName)
                        + getClassSuffix() + ".java";
    }

    public synchronized void readDbMetaData(DatabaseConfig dbConfig) throws SQLException, InstantiationException, IOException, IllegalAccessException {
        IDbHelper helper = TableGen.getDbHelper(dbConfig);
        DatabaseMetaData metaData = helper.getConnection().getMetaData();
        setInfo.getTableData(config, helper, metaData);
        setInfo.getOnFlyViewData(config, helper);
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
    abstract String getTemplateFileName();



    public String getPojoClassName(String tableName) {
        String classname = setInfo.tableName2ClassName(tableName);
        if (tableName.equals(classname)) {
            return config.getAddPrefix() + firstUp(removePrefix(tableName, config.getRemovePrefix()));
        } else {
            return config.getAddPrefix() + classname;
        }
    }

    protected  String removePrefix(String tableName, String removePrefix){
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
            e.printStackTrace();
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

        Template template = handlebars.compile(getTemplateFileName());

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
            if (setInfo.getTableInfos().get(tableName) != null) {
                v.put("tableInfo", setInfo.getTableInfos().get(tableName));
                v.put("fields", setInfo.getTableCols(tableName));
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

        if(isView) {
            setViewContext(v, tableName);
        }
        return v;
    }


    protected void setViewContext(Map<String, Object> map, String tableName) {
        map.put("tableInfo", setInfo.getViewInfos().get(tableName));
        map.put("fields", setInfo.getViewInfos().get(tableName).getFields());
        map.put("viewModifier","."+ VIEW_DIR);
    }

    protected Handlebars getHandlebars() {
        TemplateLoader loader = new ClassPathTemplateLoader();
        loader.setPrefix(getTemplateDir());
        loader.setSuffix(".hbs");

        Handlebars handlebars = new Handlebars(loader);
        handlebars.registerHelper("firstUp", (name, options) -> NameUtils.firstUp((String) name));
        handlebars.registerHelper("getPojoClassName", (tableName, options) -> this.getPojoClassName((String) tableName));
        handlebars.registerHelper("constantName", (name, options) -> NameUtils.columnNameToConstantName((String) name));
        handlebars.registerHelper("firstLow", (name, options) -> NameUtils.firstLow((String) name));
        handlebars.registerHelper("join", (items, options) -> StringUtils.join((Object[]) items, options.param(0)));
        handlebars.registerHelper("typeDefaultValue", (typeName, options) -> TableGen.defaultTypeValue().get(typeName));
        handlebars.registerHelper("unboxType", (type, options) -> TableGen.unboxType((String) type));

        handlebars.registerHelpers(ConditionalHelpers.class);

        //copied from AssignHelper
        handlebars.registerHelper("let", (String variableName, Options options) -> {
            CharSequence finalValue = options.apply(options.fn);
            options.context.data(variableName, finalValue.toString().trim());
            return null;
        });

        additionalSetting(handlebars);
        return handlebars;
    }

    private String getTemplateDir() {
        return TEMPLATE_ROOT+"/"+  (esConfig==null?
                (config.getR2dc() ? "r2dbc" : "jdbc")
                :"es");
    }

    String getFirstKeyType(String tableName) {
        try {
            List<String> keyData = getKeyData(tableName);
            List<ColumnData> columnData = getColumnData(tableName);
            return BaseGenerator.getFirstKeyType(keyData, columnData);
        } catch (GenException e) {
            e.printStackTrace();
        }
        return "";
    }

    private List<ColumnData> getColumnData(String tableName) {
        List<ColumnData> columnData = isView ? setInfo.getViewInfos().get(tableName).getFields() :
                setInfo.getTableInfos().get(tableName).getFields();
        return columnData;
    }

    protected List<String> getKeyData(String tableName) {
        List<String> keys =  setInfo.getTableKeys(tableName);
        if(keys.isEmpty() && isView){
            keys = new ArrayList<>();
            keys.add(getColumnData(tableName).get(0).getName());
        }
        return keys;
    }


    public static String getFirstKeyName(List<String> keyData2) {
        if (isNotEmpty(keyData2)) {
            return keyData2.get(0);
        }
        return null;
    }


    public static boolean containsColName(List<ColumnData> cols, String colName) {
        for (ColumnData col : cols) {
            if (col.getName().equalsIgnoreCase(colName)) {
                return true;
            }
        }
        return false;
    }


}
