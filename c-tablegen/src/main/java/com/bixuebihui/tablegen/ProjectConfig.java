package com.bixuebihui.tablegen;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;

/**
 * @author xwx
 */
public class ProjectConfig {
    private static final Logger LOG = LoggerFactory.getLogger(ProjectConfig.class);

    /**
     * Property key for enabling smart merge of generated code with existing files.
     * When enabled, user modifications in Dto, SearchCmd, and Converter files will be preserved.
     */
    public static final String ENABLE_MERGE_PROPERTY = "enable_merge";

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
    String sharedModelGroup;
    boolean indexes;
    boolean overWriteAll = false;
    boolean useCustomMetaTable = false;
    boolean useAnnotation = false;
    boolean useSwagger = true;
    boolean useAutoincrement = false;
    boolean generateProcedures = true;
    boolean generateAll = false;
    boolean r2dbc = false;
    boolean readWriteSeparate = false;
    boolean foreignKeys = false;
    /**
     * Whether to enable smart merge when generating code.
     * When enabled, existing user modifications in Dto, SearchCmd, and Converter files will be preserved.
     * Default is false to maintain backward compatibility.
     */
    boolean enableMerge = false;
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
    Map<String, List<String>> encryptedFields;
    Map<String, List<String>> ignoreFields;
    private String serviceDir;
    private String controllerDir;
    private String dtoDir;
    private String cmdDir;
    /**
     * Swagger Version 2  或 3
     */
    private String swaggerVersion;
    /**
     * API doc provider: swagger or springdoc.
     * springdoc mode does not generate swagger annotations.
     */
    private String apiDocProvider = "springdoc";

    public static ProjectConfig readFrom(Properties props, String baseDir) {
        ProjectConfig c = new ProjectConfig();

        c.alias = props.getProperty("alias");
        c.baseDir = baseDir;
        c.schema = props.getProperty("schema");
        LOG.debug("schema:{}", c.schema);
        c.catalog = props.getProperty("catalog");
        LOG.debug("catalog:{}", c.catalog);

        String baseSrc = props.getProperty("src_dir");

        c.srcDir = baseDir + baseSrc;
        LOG.debug("src_dir:{}", c.srcDir);
        c.resourceDir = baseDir + props.getProperty("resource_dir", baseSrc);
        LOG.debug("resource_dir:{}", c.resourceDir);

        c.serviceDir = baseDir + props.getProperty("service_dir", baseSrc);
        LOG.debug("service_dir:{}", c.serviceDir);

        c.controllerDir = baseDir + props.getProperty("controller_dir", baseSrc);
        LOG.debug("controller_dir:{}", c.controllerDir);

        c.dtoDir = baseDir + props.getProperty("dto_dir", baseSrc);
        LOG.debug("dto_dir:{}", c.dtoDir);

        c.cmdDir = baseDir + props.getProperty("cmd_dir", baseSrc);
        LOG.debug("cmd_dir:{}", c.cmdDir);


        c.delColName = props.getProperty("del_col_name", c.delColName);

        c.versionColName = props.getProperty("del_col_name", c.versionColName);


        c.testDir = baseDir + props.getProperty("test_dir");
        LOG.debug("test_dir:{}", c.testDir);
        c.jspDir = baseDir + props.getProperty("jsp_dir");
        LOG.debug("jsp_dir:{}", c.jspDir);

        c.packageName = props.getProperty("package_name");
        LOG.debug("package_name:{}", c.packageName);

        c.tableOwner = props.getProperty("table_owner");
        LOG.debug("table_owner:{}", c.tableOwner);

        c.sharedModelGroup = props.getProperty("shared_model_group");
        LOG.debug("shared_model_group:{}", c.sharedModelGroup);

        c.indexes = getBooleanCfg(props, "indexes");
        c.foreignKeys = getBooleanCfg(props, "foreign_keys");

        c.r2dbc = getBooleanCfg(props, "r2dbc");

        c.readWriteSeparate = getBooleanCfg(props, "read_write_separate");

        c.useCustomMetaTable = getBooleanCfg(props, "use_custom_meta_table");

        c.generateProcedures = getBooleanCfg(props, "generate_procedures");

        c.addPrefix = props.getProperty("add_prefix");
        if (c.addPrefix == null) {
            c.addPrefix = "";
        }
        c.removePrefix = props.getProperty("remove_prefix");
        if (c.removePrefix == null) {
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

        c.pojoNodeInterfaceList = makeList(props.getProperty("pojo_node_interface_list"));
        c.pojoVersionInterfaceList = makeList(props.getProperty("pojo_version_interface_list"));
        c.pojoStateInterfaceList = makeList(props.getProperty("pojo_state_interface_list"));
        c.pojoUuidInterfaceList = makeList(props.getProperty("pojo_uuid_interface_list"));
        c.pojoModifyDateInterfaceList = makeList(props.getProperty("pojo_modifydate_interface_list"));

        c.overWriteAll = getBooleanCfg(props, "over_write_all");
        c.useAnnotation = getBooleanCfg(props, "use_annotation");

        c.swaggerVersion = props.getProperty("swagger_version", "2");
        String apiDocProvider = props.getProperty("api_doc_provider");
        if (StringUtils.isBlank(apiDocProvider)) {
            // Keep backward compatibility for old configs that only set swagger_version.
            apiDocProvider = props.containsKey("swagger_version") ? "swagger" : "springdoc";
        }
        c.apiDocProvider = apiDocProvider.trim().toLowerCase(Locale.ROOT);
        if ("springdoc".equals(c.apiDocProvider)) {
            c.useSwagger = false;
        } else {
            c.useSwagger = !"no".equalsIgnoreCase(props.getProperty("use_swagger", "yes"));
        }


        c.useAutoincrement = getBooleanCfg(props, "use_autoincrement");
        c.generateAll = getBooleanCfg(props, "generate_all");

        c.extraSetting = props.getProperty("extra_setting");

        c.encryptedFields = toMap(props.getProperty("encrypted_fields"));
        c.ignoreFields = toMap(props.getProperty("ignore_fields"));

        // Check properties file first, then system property as fallback
        String enableMergeProp = props.getProperty(ENABLE_MERGE_PROPERTY);
        if (enableMergeProp != null) {
            c.enableMerge = getBooleanCfg(props, ENABLE_MERGE_PROPERTY);
        } else {
            // Fallback to system property if not set in properties file
            String sysProp = System.getProperty("tablegen.enableMerge");
            c.enableMerge = "true".equalsIgnoreCase(sysProp);
        }
        LOG.debug("enable_merge:{}", c.enableMerge);

        return c;
    }

    private static Map<String, List<String>> toMap(String encryptedFields) {
        Map<String, List<String>> res = new HashMap<>();
        if(StringUtils.isBlank(encryptedFields)) {
            return emptyMap();
        }

        String formatError= "encrypt field setting format error: must be `encrypted_fields=table1:field1,field2;table2:field3`";
        Arrays.stream(encryptedFields.split(";")).forEach(it -> {
            if (StringUtils.isNotBlank(it) && it.contains(":")) {
                String[] tableFieldPair = it.split(":");
                if (tableFieldPair.length == 2) {
                    if (res.get(tableFieldPair[0]) == null) {
                        res.put(tableFieldPair[0], new ArrayList<>(Arrays.stream(tableFieldPair[1].split(",")).collect(toList())));
                    } else {
                        res.get(tableFieldPair[0]).addAll(Arrays.stream(tableFieldPair[1].split(",")).collect(toList()));                 }
                }else{
                    LOG.error(formatError);
                }
            }else{
                LOG.error(formatError);
            }
        });
        return res;
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

    public boolean isForeignKeys() {
        return foreignKeys;
    }

    public void setForeignKeys(boolean foreignKeys) {
        this.foreignKeys = foreignKeys;
    }

    public Map<String, List<String>> getEncryptedFields() {
        return encryptedFields;
    }

    public void setEncryptedFields(Map<String, List<String>> encryptedFields) {
        this.encryptedFields = encryptedFields;
    }
    public Map<String, List<String>> getIgnoreFields() {
        return ignoreFields;
    }
    public void setIgnoreFields(Map<String, List<String>> ignoreFields) {
        this.ignoreFields = ignoreFields;
    }

    public String getSwaggerVersion() {
        return swaggerVersion;
    }

    public void setSwaggerVersion(String swaggerVersion) {
        this.swaggerVersion = swaggerVersion;
    }

    public String getApiDocProvider() {
        return apiDocProvider;
    }

    public void setApiDocProvider(String apiDocProvider) {
        this.apiDocProvider = apiDocProvider;
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
//                LOG.info(name);
            }
        }
    }

    /**
     * view list is separated by semicolon(;) and before first colon(:) is the view name.
     * The view is virtual view, i.e. it is a select SQL.
     */
    void parseViewNames(String names) {
        if (names != null) {
            if (viewList == null) {
                viewList = new HashMap<>();
            }
            StringTokenizer st = new StringTokenizer(names, ";");

            while (st.hasMoreElements()) {
                String name = st.nextToken().trim();
                viewList.put(name.substring(0, name.indexOf(":")), parseViewBody(name.substring(name.indexOf(":") + 1)));
//                LOG.info(name);
            }
        }
    }

    /**
     * if sqlOrFileName starts with @ symbol, then treat it as a file name in a relative path of tablegen.properties,
     * otherwise as sql string.
     *
     * @param sqlOrFileName file name or sql string
     * @return sql string
     */
    private String parseViewBody(String sqlOrFileName) {
        if (sqlOrFileName.startsWith("@")) {
            String fileName = baseDir + sqlOrFileName.substring(1);
            try {
                return FileUtils.readFileToString(new File(fileName), StandardCharsets.UTF_8);
            } catch (IOException e) {
                LOG.error("parseViewBody read file error:" + fileName + "\n because the `" + sqlOrFileName + "` begin with @, treat as a file name", e);
                return sqlOrFileName;
            }
        } else {
            return sqlOrFileName;
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

    public void setRemovePrefix(String removePrefix) {
        this.removePrefix = removePrefix;
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

    public String getSharedModelGroup() {
        return sharedModelGroup;
    }

    public void setSharedModelGroup(String sharedModelGroup) {
        this.sharedModelGroup = sharedModelGroup;
    }

    public boolean isEnableMerge() {
        return enableMerge;
    }

    public void setEnableMerge(boolean enableMerge) {
        this.enableMerge = enableMerge;
    }
}
