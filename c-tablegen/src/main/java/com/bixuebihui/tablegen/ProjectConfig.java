package com.bixuebihui.tablegen;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * @author xwx
 */
public class ProjectConfig {
    private static final Logger LOG = LoggerFactory.getLogger(ProjectConfig.class);

    /**
     * used for DataSource alias for spring autowired @Qualifer('alias') annotation
     */
    String alias;

    String catalog;
    String tableOwner;
    String packageName;
    String baseDir;
    String srcDir;
    String testDir;
    String resourceDir;
    String jspDir;
    String schema;
    String addPrefix;
    String removePrefix;
    String versionColName = "version";
    String delColName = "is_del";
    boolean indexes;
    boolean overWriteAll = false;
    boolean useCustomMetaTable = false;
    boolean useAnnotation = false;
    boolean useSwagger = true;
    boolean useAutoincrement = false;
    boolean generateProcedures = true;
    boolean generateAll=false;
    boolean r2dbc=false;
    boolean readWriteSeparate=false;
    /**
     * additional settings, used for comments, data checking, interfaces, etc.
     */
    String extraSetting;
    /**
     * If the extended table is available, each table can implement the following interfaces
     */
    String pojoNodeInterface;
    String pojoVersionInterface;
    String pojoStateInterface;
    String pojoUuidInterface;
    String pojoModifyDateInterface;
    List<String> pojoNodeInterfaceList;
    List<String> pojoVersionInterfaceList;
    List<String> pojoStateInterfaceList;
    List<String> pojoUuidInterfaceList;
    List<String> pojoModifyDateInterfaceList;
    /**
     * names of tables to generate for. If null we do all.
     */
    Map<String, String> tablesList;
    Map<String, String> viewList;
    Map<String, String> excludeTablesList;
    private String serviceDir;
    private String controllerDir;
    private String dtoDir;
    private String cmdDir;
    /**
     * Swagger Version 2  或 3
     */
    private String swaggerVersion;

    public static ProjectConfig readFrom(Properties props, String baseDir) {
        ProjectConfig c = new ProjectConfig();

        c.alias = props.getProperty("alias");
        c.baseDir = baseDir;
        c.schema = props.getProperty("schema");
        LOG.debug("schema:" +  c.schema);
        c.catalog = props.getProperty("catalog");
        LOG.debug("catalog:" +  c.catalog);

        String baseSrc = props.getProperty("src_dir");

        c.srcDir = baseDir + baseSrc;
        LOG.debug("src_dir:" +  c.srcDir);
        c.resourceDir = baseDir + props.getProperty("resource_dir", baseSrc);
        LOG.debug("resource_dir:" +  c.resourceDir);

        c.serviceDir = baseDir + props.getProperty("service_dir",baseSrc );
        LOG.debug("service_dir:" +  c.serviceDir);

        c.controllerDir = baseDir + props.getProperty("controller_dir", baseSrc);
        LOG.debug("controller_dir:" +  c.controllerDir);

        c.dtoDir = baseDir + props.getProperty("dto_dir", baseSrc);
        LOG.debug("dto_dir:" +  c.dtoDir);

        c.cmdDir = baseDir + props.getProperty("cmd_dir", baseSrc);
        LOG.debug("cmd_dir:" +  c.cmdDir);


        c.delColName = props.getProperty("del_col_name", c.delColName);

        c.versionColName = props.getProperty("del_col_name", c.versionColName);


        c.testDir = baseDir + props.getProperty("test_dir");
        LOG.debug("test_dir:" +  c.testDir);
        c.jspDir = baseDir + props.getProperty("jsp_dir");
        LOG.debug("jsp_dir:" +  c.jspDir);

        c.packageName = props.getProperty("package_name");
        LOG.debug("package_name:" +  c.packageName);

        c.tableOwner = props.getProperty("table_owner");
        LOG.debug("table_owner:" +  c.tableOwner);

        c.indexes = getBooleanCfg(props, "indexes");
        c.r2dbc = getBooleanCfg(props, "r2dbc");

        c.readWriteSeparate = getBooleanCfg(props, "read_write_separate");

        c.useCustomMetaTable = getBooleanCfg(props, "use_custom_meta_table");

        c.generateProcedures = getBooleanCfg(props, "generate_procedures");

        c.addPrefix = props.getProperty("add_prefix");
        if ( c.addPrefix == null) {
            c.addPrefix = "";
        }
        c.removePrefix = props.getProperty("remove_prefix");
        if ( c.removePrefix == null) {
            c.removePrefix = "";
        }

        c.parseTableNames(props.getProperty("table_list"));
        c.parseViewNames(props.getProperty("view_list"));
        c.parseExcludeTableNames(props.getProperty("exclude_table_list"));

        // 有扩展表时用以下interface的设置值
        c.pojoNodeInterface = props.getProperty("pojo_node_interface");
        c.pojoVersionInterface = props.getProperty("pojo_version_interface");
        c.pojoStateInterface = props.getProperty("pojo_state_interface");
        c.pojoUuidInterface = props.getProperty("pojo_uuid_interface");
        c.pojoModifyDateInterface = props.getProperty("pojo_modifydate_interface");

        c.pojoNodeInterfaceList =  makeList(props.getProperty("pojo_node_interface_list"));
        c.pojoVersionInterfaceList = makeList(props.getProperty("pojo_version_interface_list"));
        c.pojoStateInterfaceList = makeList(props.getProperty("pojo_state_interface_list"));
        c.pojoUuidInterfaceList = makeList(props.getProperty("pojo_uuid_interface_list"));
        c.pojoModifyDateInterfaceList = makeList(props.getProperty("pojo_modifydate_interface_list"));

        c.overWriteAll = getBooleanCfg(props, "over_write_all");
        c.useAnnotation = getBooleanCfg(props, "use_annotation");

        c.swaggerVersion = props.getProperty("swagger_version", "2");


        c.useAutoincrement = getBooleanCfg(props, "use_autoincrement");
        c.generateAll = getBooleanCfg(props, "generate_all");

        c.extraSetting = props.getProperty("extra_setting");

        return c;
    }

    private static boolean getBooleanCfg(Properties props, String key) {
        return "yes".equalsIgnoreCase(props.getProperty(key));
    }

    private static List<String> makeList(String property) {
        List<String> res = new ArrayList<>();
        if (StringUtils.isNotEmpty(property)) {
            res.addAll(Arrays.asList(property.trim().split(",")));
        }
        return res;
    }

    public String getSwaggerVersion() {
        return swaggerVersion;
    }

    public void setSwaggerVersion(String swaggerVersion) {
        this.swaggerVersion = swaggerVersion;
    }

    public String getDtoDir() {
        return dtoDir;
    }

    public void setDtoDir(String dtoDir) {
        this.dtoDir = dtoDir;
    }

    public String getCmdDir() {
        return cmdDir;
    }

    public void setCmdDir(String cmdDir) {
        this.cmdDir = cmdDir;
    }

    public Map<String, String> getViewList() {
        return viewList;
    }

    public void setViewList(Map<String, String> viewList) {
        this.viewList = viewList;
    }

    public boolean isUseSwagger() {
        return useSwagger;
    }

    public void setUseSwagger(boolean useSwagger) {
        this.useSwagger = useSwagger;
    }

    public String getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getTableOwner() {
        return tableOwner;
    }

    public void setTableOwner(String tableOwner) {
        this.tableOwner = tableOwner;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getSrcDir() {
        return srcDir;
    }

    public void setSrcDir(String srcDir) {
        this.srcDir = srcDir;
    }

    public String getTestDir() {
        return testDir;
    }

    public void setTestDir(String testDir) {
        this.testDir = testDir;
    }

    public String getResourceDir() {
        return resourceDir;
    }

    public void setResourceDir(String resourceDir) {
        this.resourceDir = resourceDir;
    }

    public String getJspDir() {
        return jspDir;
    }

    public void setJspDir(String jspDir) {
        this.jspDir = jspDir;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getAddPrefix() {
        return addPrefix;
    }

    public void setAddPrefix(String addPrefix) {
        this.addPrefix = addPrefix;
    }

    public String getVersionColName() {
        return versionColName;
    }

    public void setVersionColName(String versionColName) {
        this.versionColName = versionColName;
    }

    public boolean isIndexes() {
        return indexes;
    }

    public void setIndexes(boolean indexes) {
        this.indexes = indexes;
    }

    public boolean isOverWriteAll() {
        return overWriteAll;
    }

    public void setOverWriteAll(boolean overWriteAll) {
        this.overWriteAll = overWriteAll;
    }

    public boolean isUseCustomMetaTable() {
        return useCustomMetaTable;
    }

    public void setUseCustomMetaTable(boolean useCustomMetaTable) {
        this.useCustomMetaTable = useCustomMetaTable;
    }

    public boolean isUseAnnotation() {
        return useAnnotation;
    }

    public void setUseAnnotation(boolean useAnnotation) {
        this.useAnnotation = useAnnotation;
    }

    public boolean isUseAutoincrement() {
        return useAutoincrement;
    }

    public void setUseAutoincrement(boolean useAutoincrement) {
        this.useAutoincrement = useAutoincrement;
    }

    public boolean isGenerateProcedures() {
        return generateProcedures;
    }

    public void setGenerateProcedures(boolean generateProcedures) {
        this.generateProcedures = generateProcedures;
    }

    public boolean isGenerateAll() {
        return generateAll;
    }

    public void setGenerateAll(boolean generateAll) {
        this.generateAll = generateAll;
    }

    public String getExtraSetting() {
        return extraSetting;
    }

    public void setExtraSetting(String extraSetting) {
        this.extraSetting = extraSetting;
    }

    public String getPojoNodeInterface() {
        return pojoNodeInterface;
    }

    public void setPojoNodeInterface(String pojoNodeInterface) {
        this.pojoNodeInterface = pojoNodeInterface;
    }

    public String getPojoVersionInterface() {
        return pojoVersionInterface;
    }

    public void setPojoVersionInterface(String pojoVersionInterface) {
        this.pojoVersionInterface = pojoVersionInterface;
    }

    public String getPojoStateInterface() {
        return pojoStateInterface;
    }

    public void setPojoStateInterface(String pojoStateInterface) {
        this.pojoStateInterface = pojoStateInterface;
    }

    public String getPojoUuidInterface() {
        return pojoUuidInterface;
    }

    public void setPojoUuidInterface(String pojoUuidInterface) {
        this.pojoUuidInterface = pojoUuidInterface;
    }

    public String getPojoModifyDateInterface() {
        return pojoModifyDateInterface;
    }

    public void setPojoModifyDateInterface(String pojoModifyDateInterface) {
        this.pojoModifyDateInterface = pojoModifyDateInterface;
    }

    public List<String> getPojoNodeInterfaceList() {
        return pojoNodeInterfaceList;
    }

    public void setPojoNodeInterfaceList(List<String> pojoNodeInterfaceList) {
        this.pojoNodeInterfaceList = pojoNodeInterfaceList;
    }

    public List<String> getPojoVersionInterfaceList() {
        return pojoVersionInterfaceList;
    }

    public void setPojoVersionInterfaceList(List<String> pojoVersionInterfaceList) {
        this.pojoVersionInterfaceList = pojoVersionInterfaceList;
    }

    public List<String> getPojoStateInterfaceList() {
        return pojoStateInterfaceList;
    }

    public void setPojoStateInterfaceList(List<String> pojoStateInterfaceList) {
        this.pojoStateInterfaceList = pojoStateInterfaceList;
    }

    public List<String> getPojoUuidInterfaceList() {
        return pojoUuidInterfaceList;
    }

    public void setPojoUuidInterfaceList(List<String> pojoUuidInterfaceList) {
        this.pojoUuidInterfaceList = pojoUuidInterfaceList;
    }

    public List<String> getPojoModifyDateInterfaceList() {
        return pojoModifyDateInterfaceList;
    }

    public void setPojoModifyDateInterfaceList(List<String> pojoModifyDateInterfaceList) {
        this.pojoModifyDateInterfaceList = pojoModifyDateInterfaceList;
    }

    public Map<String, String> getTablesList() {
        return tablesList;
    }

    public void setTablesList(Map<String, String> tablesList) {
        this.tablesList = tablesList;
    }

    public Map<String, String> getExcludeTablesList() {
        return excludeTablesList;
    }

    public void setExcludeTablesList(Map<String, String> excludeTablesList) {
        this.excludeTablesList = excludeTablesList;
    }

    /**
     * Parses a comma separated list of table names into the tablesList List.
     */
    void parseTableNames(String names) {
        if (names != null) {
            if (tablesList == null) {
                tablesList = new Hashtable<>();
            }
            StringTokenizer st = new StringTokenizer(names, ",");
            String name;
            while (st.hasMoreElements()) {
                name = st.nextToken().trim();
                tablesList.put(name, name);
                LOG.info(name);
            }
        }
    }

    /**
     *  view list is separated by semicolon(;) and before first colon(:) is the view name.
     *  The view is virtual view, i.e. it is a select SQL.
     */
    void parseViewNames(String names) {
        if (names != null) {
            if (viewList == null) {
                viewList = new HashMap<>();
            }
            StringTokenizer st = new StringTokenizer(names, ";");

            while (st.hasMoreElements()) {
                String name = st.nextToken().trim();
                viewList.put(name.substring(0,name.indexOf(":")), name.substring(name.indexOf(":")+1));
                LOG.info(name);
            }
        }
    }

    /**
     * Parses a comma separated list of table names into the tablesList List.
     */
    void parseExcludeTableNames(String names) {
        if (names != null) {
            if (excludeTablesList == null) {
                excludeTablesList = new Hashtable<>();
            }
            StringTokenizer st = new StringTokenizer(names, ",");
            String name;
            while (st.hasMoreElements()) {
                name = st.nextToken().trim();
                excludeTablesList.put(name, name);
                LOG.info("exclude table: " + name);
            }
        }
    }

    public String getBaseSrcDir() {
        return srcDir + File.separator + packageName2Dir(packageName);
    }

    public String packageName2Dir(String packageName) {
        if (packageName == null) {
            return null;
        } else {
            return packageName.replaceAll("\\.", "\\" + File.separator);
        }
    }

    public String getRemovePrefix() {
        return removePrefix;
    }

    public boolean getR2dc() {
        return this.r2dbc;
    }

    public String getBaseControllerDir() {
        return controllerDir + File.separator + packageName2Dir(packageName);
    }
    public String getBaseCmdDir() {
        return cmdDir + File.separator + packageName2Dir(packageName);
    }
    public String getBaseDtoDir() {
        return dtoDir + File.separator + packageName2Dir(packageName);
    }

    public String getBaseServiceDir() {
        return serviceDir + File.separator + packageName2Dir(packageName);
    }
    public String getBaseServiceImplDir() {
        return serviceDir + File.separator + packageName2Dir(packageName);
    }

    public String getDelColName() {
        return delColName;
    }

    public void setDelColName(String delColName) {
        this.delColName = delColName;
    }
}
