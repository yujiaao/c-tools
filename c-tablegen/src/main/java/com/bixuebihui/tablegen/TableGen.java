package com.bixuebihui.tablegen;

/**
 * TableGen
 * <p>A generator of container classes for a database.
 * This application uses the database metadata to create
 * one object per table designed to hold the type of data
 * stored within that table.</p>
 * It also generate methods to update,retrieve and insert
 * the object data within the database. The update and
 * retrieve methods come in two flavours, ByKey and ByIndex.</p>
 * <p>If keys are found on a table then updateByKey and
 * retrieveByKey methods are created</p>
 * <p>If foreign_keys are found on the table retrieveByXXX methods are created for each key
 * as well
 * <p>If indexes are found on a table then updateByIndex and
 * retrieveByIndex methods are created</p>
 * insert and getFromResultSet methods are always created.
 *
 * @author J.A.Carter, Xing Wanxiang
 * @version 1.6
 * (c) Joe Carter 1998 , Modified by Xwx
 * Released under GPL. See LICENSE for full details.
 */

import com.bixuebihui.cache.DictionaryCache;
import com.bixuebihui.cache.DictionaryItem;
import com.bixuebihui.datasource.BitmechanicDataSource;
import com.bixuebihui.dbcon.DatabaseConfig;
import com.bixuebihui.generated.tablegen.pojo.T_metacolumn;
import com.bixuebihui.jdbc.DbHelper;
import com.bixuebihui.jdbc.IDbHelper;
import com.bixuebihui.tablegen.dbinfo.ProcedureGen;
import com.bixuebihui.tablegen.dbinfo.ProcedureInfo;
import com.bixuebihui.tablegen.dbinfo.ProcedureParameterInfo;
import com.bixuebihui.tablegen.dbinfo.ProcedureUtils;
import com.bixuebihui.tablegen.diffhandler.DiffHandler;
import com.bixuebihui.tablegen.entry.ColumnData;
import com.bixuebihui.tablegen.entry.TableInfo;
import com.bixuebihui.tablegen.entry.TableSetInfo;
import com.bixuebihui.tablegen.generator.GenerateAll;
import com.bixuebihui.tablegen.generator.PojoGenerator;
import com.bixuebihui.util.DbUtils;
import com.bixuebihui.util.other.CMyFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.text.CaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.*;

import static com.bixuebihui.tablegen.NameUtils.*;
import static com.bixuebihui.tablegen.TableGenConfig.PROPERTIES_FILENAME;
import static com.bixuebihui.tablegen.generator.DalGenerator.*;

/**
 * 思路决定出路，创新才有发展，整合才能壮大
 *
 * @author xingwx
 */
public class TableGen implements DiffHandler {
    public static final String MANAGER_SUFFIX = "Manager";
    public static final String INDENT = "      ";
    public static final String BUSINESS = "business";
    private static final Logger LOG = LoggerFactory.getLogger(TableGen.class);
    static String[] genericFiles = {"BaseList"};
    TableSetInfo setInfo = new TableSetInfo();
    BufferedWriter currentOutput;
    /**
     * Writes out the variables initial value if not null able.
     */
    Map<String, String> typeDefaultValue = defaultTypeValue();
    private ProjectConfig config;
    private DatabaseConfig dbConfig;
    /**
     * Whether the generation is successful
     */
    private boolean generateSuccess = true;
    private boolean traceEnable = false;
    private PrintStream console = System.out;

    public TableGen(OutputStream outputStream) {
        initConsole(outputStream);
    }

    public TableGen() {
    }

    /**
     * Application start.
     *
     * @throws SQLException db error
     */
    public static void main(String[] argv) throws SQLException {
        TableGen us = new TableGen();

        if (argv.length >= 1) {
            if ("--help".equals(argv[0])
                    || !Files.exists(Path.of(argv[0]))) {
                usageAndExit(0);
                return;
            }
            us.run(argv[0]);
        } else {
            us.run(null);
        }

    }

    /**
     * Displays the usage of the TableGen program and exits with the error code.
     */
    public static void usageAndExit(int code) {
        System.err.println("Usage: java com.bixuebihui.tablegen.TableGen properties_file_name");

        try {
            System.out.println(IOUtils.resourceToString("/usage.md", StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.exit(code);
    }

    public static Map<String, String> defaultTypeValue() {
        Map<String, String> map = new HashMap<>(16);
        map.put("String", "\"\"");
        map.put("int", "0");
        map.put("byte", "0");
        map.put("byte[]", "new byte[]{0}");

        map.put("long", "0");
        map.put("Integer", "0");
        map.put("Short", "0");
        map.put("Byte", "(byte)0");
        map.put("Long", "0L");
        map.put("Date", "new Date(0)");
        map.put("Timestamp", "new Timestamp(System.currentTimeMillis())");
        map.put("Boolean", "Boolean.FALSE");
        map.put("Double", "0.0");
        map.put("Float", "0.0F");
        map.put("LocalDateTime", "LocalDateTime.now()");
        map.put("LocalDate", "LocalDate.now()");
        map.put("LocalTime", "LocalTime.now()");
        return map;
    }

    public static IDbHelper getDbHelper(DatabaseConfig dbconfig) {
        DbHelper db = new DbHelper();
        BitmechanicDataSource ds = new BitmechanicDataSource();
        ds.setDatabaseConfig(dbconfig);
        db.setDataSource(ds);
        return db;
    }

    /**
     * only keys used as parameters in where clause, so here params mean key columns.
     *
     * @param params     keys of table
     * @param withLike   like for true or equal for false
     * @param columnData columns of table
     * @return string starts with 'where' or empty string
     * @throws GenException
     */
    public static String createPreparedWhereClause(List<String> params, boolean withLike, List<ColumnData> columnData) {
        StringBuilder where = new StringBuilder();

        // if we have keys passed in then we add a "where" to the count
        // e.g. se
        //
        if (params != null) {
            // work out the "where" part of the update first..
            //
            where.append(" where ");
            String key;
            String keyType;
            for (Iterator<String> e = params.iterator(); e.hasNext(); ) {
                key = e.next();
                keyType = getColType(key, columnData);

                // only allow likes on String types...
                // Hmmm should we allow more than this???
                //
                if ((withLike) && ("String".equals(keyType))) {
                    where.append(key).append(" like ?");
                } else {
                    where.append(key).append("=?");
                }

                if (e.hasNext()) {
                    where.append(" and ");
                }
            }

        }

        return where.toString();
    }

    public ProjectConfig getConfig() {
        return config;
    }

    public DatabaseConfig getDbConfig() {
        return dbConfig;
    }

    public void setDbConfig(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    private void initConsole(OutputStream out) {
        if (out instanceof PrintStream) {
            console = (PrintStream) out;
        } else if (out == System.out) {
            //do nothing
        } else {
            console = new PrintStream(out);
        }

    }

    public void info(String message) {
        console.println(message);
    }

    public void trace(String message) {
        if (traceEnable) {
            console.println(message);
        }
    }

    public void run(String filename) throws SQLException {
        run(filename, System.out);
    }

    @SuppressWarnings("unchecked")
    public static synchronized Map<String, List<ColumnData>> getTableDataFromLocalCache() {

        String path = getCachedTableDataFilePath();
        File file = new File(path);
        if (file.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {

                Map<String, List<ColumnData>> v = (Map<String, List<ColumnData>>) in.readObject();

                if (v == null) {
                    return new HashMap<>(16);
                }
                return v;

            } catch (ClassNotFoundException | IOException e) {
                LOG.warn("",e);
            }
        }

        return new HashMap<>(16);

    }

    private static String getCachedTableDataFilePath() {
        String baseDir = System.getProperty("user.dir");

        String srcDir = "target";

        return baseDir + File.separator + srcDir + File.separator + "gen_table_data.cache";
    }

    private void generateSpringXml() {

        info("Generating spring-dal.xml (if it exists before) : ");

        String fileName = this.config.resourceDir + File.separator + "config" + File.separator + "spring-dal.xml";
        try {
            Map<String, String> beans = new HashMap<>(16);
            for (TableInfo table : setInfo.getTableInfos().values()) {
                beans.put(firstLow(table.getName())
                        + MANAGER_SUFFIX, getBusFullClassName(table.getName()));
            }
            File f = new File(fileName);
            String xml = null;
            if (f.exists()) {
                xml = FileUtils.readFileToString(f, "UTF-8");
            }

            if (StringUtils.trimToNull(xml) == null) {
                xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<beans xmlns=\"http://www.springframework.org/schema/beans\"\n"
                        + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:context=\"http://www.springframework.org/schema/context\"\n"
                        + "    xmlns:aop=\"http://www.springframework.org/schema/aop\" xmlns:tx=\"http://www.springframework.org/schema/tx\"\n"
                        + "    xsi:schemaLocation=\"http://www.springframework.org/schema/beans \n"
                        + "    http://www.springframework.org/schema/beans/spring-beans-3.0.xsd\">\n" + "</beans>\n";
            }

            DalXmlEditor.write(DalXmlEditor.addBeans(xml, beans), fileName);

        } catch (IOException e) {
            LOG.warn(e.getMessage());
        }
    }

    public static synchronized void saveTableDataToLocalCache(HashMap<String, List<ColumnData>> data) {

        String path = getCachedTableDataFilePath();
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(data);
        } catch (IOException e) {
            LOG.warn("",e);
        }

    }

    public static String unboxType(String type) {
        switch (type) {
            case "Integer":
                return "(int)";
            case "Long":
                return "(long)";
            case "Short":
                return "(short)";
            case "Byte":
                return "(byte)";
            case "Boolean":
                return "(boolean)";
            case "String":
                return "\"\"+";
            default:
                return type;
        }
    }

    private String getCachedColumnDataFilePath() {

        String srcDir = "target";

        return config.getBaseDir() + File.separator + srcDir + File.separator + "gen_column_data.cache";
    }

    public void run(String filename, OutputStream out) {
        new GenerateAll().run(filename, out);
    }

    private void generateStoreProcedures(DatabaseMetaData metaData) throws SQLException {

        try {
            info("Generating Store Procedures : ");

            TableInfo table = new TableInfo("Procedures");
            String fileName = config.getBaseSrcDir() + File.separator + "stub" + File.separator + table.getName() + ".java";
            currentOutput = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(fileName), TableGenConfig.FILE_ENCODING));

            writeHeader(table, "stub", "extends BaseList<Object, Object>");

            List<ProcedureInfo> proc = ProcedureUtils.getProcedure(metaData, config.catalog, config.schema, config.tableOwner, null, null);

            for (ProcedureInfo info : proc) {
                List<ProcedureParameterInfo> rs = ProcedureUtils.getProcedureColumns(metaData, info);
                String str = ProcedureGen.process(info, rs);
                out(str);
            }

            out("public Object mapRow(ResultSet arg0, int arg1) throws SQLException {\n"
                    + "	// TODO Auto-generated method stub\n" + "	return null;\n" + "}\n"
                    + "public Object getId(Object arg0) {\n" + "	// TODO Auto-generated method stub\n"
                    + "	return null;\n" + "}\n" + "public Object getNextKey() {\n"
                    + "	// TODO Auto-generated method stub\n" + "	return null;\n" + "}\n"
                    + "public boolean insertDummy() throws SQLException {\n"
                    + "	// TODO Auto-generated method stub\n" + "	return false;\n" + "}\n"
                    + "public void setId(Object arg0, Object arg1) {\n" + "	// TODO Auto-generated method stub\n"
                    + "	\n" + "}\n" + "@Override\n" + "public String getKeyName() {\n"
                    + "	// TODO Auto-generated method stub\n" + "	return null;\n" + "}\n" + "@Override\n"
                    + "public String getTableName() {\n" + "	// TODO Auto-generated method stub\n"
                    + "	return null;\n" + "}");
            out("}");
            currentOutput.close();

        } catch (IOException e) {
            e.printStackTrace(console);
            LOG.warn("", e);
        }
    }

    protected String getTableComment(String tableName) {
        if (config.useCustomMetaTable) {
            DictionaryItem item = DictionaryCache
                    .byId(TableGenConfig.METATABLE_DICT + DictionaryCache.KEY_SEPARATOR + tableName);
            return item == null ? tableName : item.getValue();
        } else {
            return tableName;
        }
    }

    private String getColumnDescription(String tableName, ColumnData col) {
        Map<String, T_metacolumn> cols = setInfo.getColumnsExtInfo(tableName);
        return TableUtils.getColumnDescription(config, cols, tableName, col);
    }

    private void generateJsp() throws SQLException {

        try {

            for (TableInfo table : setInfo.getTableInfos().values()) {
                String tableName = table.getName();
                info("Generating JSPs : " + tableName);

                String fileName = config.jspDir + File.separator + "list" + File.separator + config.addPrefix + tableName.toLowerCase()
                        + "_list.jsp";
                currentOutput = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(fileName), TableGenConfig.FILE_ENCODING));

                String baseDir = config.packageName + ".";
                String serviceName = getPojoClassName(tableName) + "Manager";
                String controlerName = getPojoClassName(tableName) + "WebUI";

                out("<%@ page language=\"java\" import=\"java.util.*\" contentType=\"text/html; charset=UTF-8\" pageEncoding=\"UTF-8\"%>"
                        + "<%@ \npage import=\"" + baseDir + "business." + serviceName + "\"%>" + "<%@ \npage import=\""
                        + baseDir + "web." + controlerName + "\"%><%\n" + "String tableid=\"" + tableName.toLowerCase()
                        + "\";\n" + "\t" + controlerName + " c = new " + controlerName + "();\n" + "\n"
                        + "\tc.setService(new " + serviceName + "());\n" + "\tc.setId(tableid);\n"
                        + "\tc.setSuccessView(\"welcome\");\n"
                        + "\tif(null==c.handleRequestInternal(request, response)){ return;}\n" + "%>\n" + "<html>\n"
                        + "<head>\n" + "<script type=\"text/javascript\">\n" + "function onInvokeAction(id, code) {\n"
                        + "    $('#ac').val(code);\n" + "    if('edit'==code){\n"
                        + "   $('#editable').val($('#editable').val()==\"true\"?\"false\":\"true\");" + "    }\n"
                        + "\tcreateHiddenInputFieldsForLimitAndSubmit(id);\n" + "}\n"
                        + "function onInvokeAction1(id) {\n" + "    setExportToLimit(id, '');\n"
                        + "    var parameterString = createParameterStringForLimit(id);\n" + "    var url = '"
                        + tableName.toLowerCase() + "_list.jsp?decorator=ajax&ajax=true&' + parameterString;\n"
                        + "   // alert(url);\n" + "    $.get(url, function(data) {\n"
                        + "        $(\"#<%=tableid%>\").html(data)\n" + "    });\n" + "}\n" + "\n"
                        + "function onInvokeExportAction(id) {\n"
                        + "\tvar parameterString = createParameterStringForLimit(id);\n" + "\tlocation.href = '"
                        + tableName.toLowerCase() + "_list.jsp?decorator=none&ajax=true&' + parameterString;\n" + "}\n"
                        + "$(document).ready(function() {$('#selectAllChkBox').click(function(){ShiftCheck('chk', this.checked);})});"
                        + "</script>\n" + "</head>\n" + "<body>\t<form id='<%=tableid %>Form' name=\""
                        + tableName.toLowerCase() + "Form\" action=\"" + tableName.toLowerCase() + "_list.jsp\">\n"
                        + "<%=request.getAttribute(tableid)%>\n"
                        + "\t<input id=\"editable\" name=\"editable\" type=\"hidden\" value=\"<%=request.getParameter(\"editable\") %>\" />\n"
                        + "\t<input id=\"ac\" name=\"ac\" type=\"hidden\" value=\"\" />\n" + "</form>\n" + "</body>\n"
                        + "</html>");

                currentOutput.close();
            }

            // generate index list of all jsp

            info("Generating JSP index list : " + config.packageName + File.separator + "list" + File.separator
                    + "index.jsp");

            String fileName = config.jspDir + File.separator + "list" + File.separator + "index.jsp";
            currentOutput = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(fileName), TableGenConfig.FILE_ENCODING));
            // BufferedWriter(new
            out("<%@ page language=\"java\" import=\"java.util.*\" pageEncoding=\"UTF-8\"%>\n" + "<html>\n" + "<head>\n"
                    + "</head>\n" + "<body><ul>\n");

            for (TableInfo table : setInfo.getTableInfos().values()) {
                String tableName = table.getName();
                String href = config.addPrefix + tableName.toLowerCase() + "_list.jsp";

                out("\t<li><a href='" + href + "' target=\"mainForm\" >" + tableName.toLowerCase()
                        + "</a>&nbsp;<a href='" + href + "?editable=true' target=\"mainForm\" >" + "编辑</a></li>\n");

            }
            out("</ul></body>\n" + "</html>");

            currentOutput.close();

        } catch (IOException e) {
            e.printStackTrace(console);
        }
    }

    private void writeInsertDummyTest(String tableName, String methodName) throws IOException {
        out("public void test" + firstUp(methodName) + "() throws SQLException");
        out("{");
        String className = this.getPojoClassName(tableName) + "Manager";
        out("  " + className + " man = new " + className + "();");
        out("    assertTrue(man." + methodName + "());");
        out("}");
        out("");
    }

    /**
     * Constructor from a properties file. Default file is "tablegen.prop"
     *
     * <TABLE>
     * <TR>
     * <TH>Parameter
     * <TH>Description
     * </TR>
     * <TR>
     * <TD>tables_dir</TD>
     * <TD>directory where generated classes are placed.</TD>
     * </TR>
     * <TR>
     * <TD>package_name</TD>
     * <TD>package name to use for gen classes.</TD>
     * </TR>
     * <TR>
     * <TD>foreign_keys</TD>
     * <TD>(optional) Generate retrieve methods for each foreign_key
     * available</TD>
     * </TR>
     * <TR>
     * <TD>indexes</TD>
     * <TD>(optional) Generate index methods if indexes available</TD>
     * </TR>
     * <TR>
     * <TD>schema</TD>
     * <TD>(optional) DB schema to use</TD>
     * </TR>
     * <TR>
     * <TD>table_list</TD>
     * <TD>(optional) Comma separated list of tables to build</TD>
     * </TR>
     * <TR>
     * <TD>types_are_strings</TD>
     * <TD>Some drivers (e.g. MM MySql) return the types as String. If so set
     * this to yes. Leave out otherwise</TD>
     * <TR>
     * <TD>prefix</TD>
     * <TD>A string to prefix to classes names. To prevent namespace
     * problems</TD>
     * </TR>
     * </TABLE>
     */
    public synchronized void init(String filename) {
        String propertiesFilename = filename != null ? filename : PROPERTIES_FILENAME;

        try (FileInputStream fis = new FileInputStream(propertiesFilename)) {
            Properties props = new Properties();

            props.load(fis);

            dbConfig = DatabaseConfig.newInstance(props);

            config = ProjectConfig.readFrom(props, getConfigBaseDir(propertiesFilename));

        } catch (IOException e) {
            e.printStackTrace(console);
        }
    }

    /**
     * Connects to the database a get the MetaData.
     */
    public DatabaseMetaData connect(Connection cn) throws SQLException {

        DatabaseMetaData metaData = cn.getMetaData();

        info("");
        info("TableGen Database JavaBean Generator v1.91");
        info("-----------------------------------------");
        info("Database version : " + metaData.getDatabaseProductVersion());
        info("Driver Name : " + metaData.getDriverName());
        info("Driver Version : " + metaData.getDriverMajorVersion() + "." + metaData.getDriverMinorVersion());

        return metaData;
    }


    public boolean makeDir() {
        boolean res = true;
        String baseDir = config.getBaseSrcDir();
        res = res && CMyFile.makeDir(baseDir, true);
        res = res && CMyFile.makeDir(config.resourceDir + File.separator + "config", true);

        res = CMyFile.makeDir(baseDir + File.separator + "pojo", true) && res;
        res = CMyFile.makeDir(baseDir + File.separator + "dal", false) && res;
        res = CMyFile.makeDir(baseDir + File.separator + BUSINESS, false) && res;
        res = CMyFile.makeDir(baseDir + File.separator + "web", true) && res;
        res = CMyFile.makeDir(config.testDir + File.separator + config.packageName2Dir(config.packageName) + File.separator + BUSINESS,
                true) && res;
        res = CMyFile.makeDir(config.jspDir + File.separator + "list", true) && res;

        res = CMyFile.makeDir(baseDir + File.separator + "stub", false) && res;
        return res;
    }


    public void generateBaseList() throws SQLException {
        try {
            info("Generating BaseList : ");
            String baseDir = config.getBaseSrcDir();

            String fileName = baseDir + File.separator + "BaseList.java";
            File f = new File(fileName);

            if (config.overWriteAll || !f.exists()) {
                currentOutput = new BufferedWriter(
                        // new
                        new OutputStreamWriter(new FileOutputStream(fileName), TableGenConfig.FILE_ENCODING));

                writeBaseHeader();

                out("\n" +
                        "    public BaseList(DataSource ds) {\n" +
                        "        MSDbHelper dbHelper0 = new MSDbHelper();\n" +
                        "        dbHelper0.setMasterDatasource(ds);\n" +
                        "        dbHelper0.setDataSource(ds);\n" +
                        "        if (mLog.isDebugEnabled()) {\n" +
                        "            ProxyFactory obj = new ProxyFactory(dbHelper0);\n" +
                        "            obj.addAdvice(new DbHelperAroundAdvice());\n" +
                        "            dbHelper = (IDbHelper) obj.getProxy();\n" +
                        "        } else {\n" +
                        "            dbHelper = dbHelper0;\n" +
                        "        }\n" +
                        "    }");

                out("}");
                currentOutput.close();

            } else {
                info("File " + fileName + " allready exists! Skip rewrite.");
            }

        } catch (IOException e) {
            e.printStackTrace(console);
        }
    }

    /**
     * Generates the plane java model for each table.
     */
    public void generatePojos() throws SQLException, IOException {

        for (TableInfo table : setInfo.getTableInfos().values()) {
            info("Generating pojos : " + table.getName());
            generatePojo(table);
        }

    }

    private void generateTest() throws SQLException {
        try {
            for (TableInfo table : setInfo.getTableInfos().values()) {
                String tableName = table.getName();
                info("Generating TESTs : " + tableName);
                String baseDir = config.testDir + File.separator + config.packageName2Dir(config.packageName);
                String fileName = baseDir + File.separator + BUSINESS + File.separator + getPojoClassName(tableName)
                        + "ManagerTest.java";
                File f = new File(fileName);
                boolean fileExists = f.exists();
                if (!fileExists) {
                    currentOutput = new BufferedWriter(
                            new OutputStreamWriter(new FileOutputStream(fileName), TableGenConfig.FILE_ENCODING));

                    //getColumnData(tableName);
                    writeHeader(table, BUSINESS, " extends TestCase");

                    writeSelectPageTest(tableName, "select");
                    writeCountWhereTest(tableName);

                    List<String> keyData = setInfo.getTableKeys(tableName);
                    if (keyData != null && keyData.size() == 1) {
                        writeInsertDummyTest(tableName, "insertDummy");
                    }

                    out("}");
                    currentOutput.close();
                }
            }

        } catch (IOException e) {
            e.printStackTrace(console);
        }
    }

    /**
     * 生成单个pojo类
     *
     * @param table
     */
    public void generatePojo(TableInfo table) throws IOException {
        String tableName = table.getName();
        String baseDir = config.getBaseSrcDir();

        String fileName = baseDir + File.separator + "pojo" + File.separator + getPojoClassName(tableName)
                + ".java";
        try {

            currentOutput = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(fileName), TableGenConfig.FILE_ENCODING));

            List<ColumnData> colData = table.getFields();

            String interfaces = setInfo.getInterface(tableName, config);
            writeHeader(table, "pojo", interfaces + PojoGenerator.getExtendsClasses(setInfo, tableName));

            // handle the variables
            for (ColumnData columnData : colData) {
                writeVariable(tableName, columnData);
            }

            boolean findNode = false;
            // handle the variable access functions (set/get)
            for (ColumnData columnData : colData) {
                writeSetGet(tableName, columnData);
                if ("relation_code".equalsIgnoreCase(columnData.getName())) {
                    findNode = true;
                }
            }

            if (interfaces.contains("NodeInterface") && !findNode) {
                ColumnData col = new ColumnData("relation_code", 12, 0, false, false, 0, "",
                        "hack code for relation_code( to fit NodeInterface), not for use!", config.getR2dc());
                out("// " + col.getRemarks());
                out("// " + col.getComment());
                writeVariable(tableName, col);
                writeSetGet(tableName, col);
            }

            // handle the constructor
            out(" public " + getPojoClassName(tableName) + "()");
            out("     {");
            for (ColumnData columnData : colData) {
                writeInit(columnData);
            }
            out("     }");

            // handle the toXml functions
            out(" public String toXml()");
            out("     {");
            out("      StringBuilder s= new StringBuilder();");
            out("      String ln = System.getProperty(\"line.separator\");");
            out("      s.append(\"<" + tableName + " \");");
            for (ColumnData col : colData) {
                if ("String".equals(col.getJavaType())) {
                    out("     s.append(\"" + firstLow(col.getName())
                            + "=\\\"\").append(StringEscapeUtils.escapeXml11(this.get"
                            + firstUp(col.getName()) + "())).append(\"\\\" \");");
                } else {
                    out("     s.append(\"" + firstLow(col.getName()) + "=\\\"\").append(this.get"
                            + firstUp(col.getName()) + "()).append(\"\\\" \");");
                }
            }
            out("     s.append(\" />\");");
            out("     s.append(ln);");
            out("    return s.toString();");
            out("     }");

            out("}");

        } catch (IOException ex) {
            generateSuccess = false;
            ex.printStackTrace(console);
        } finally {
            currentOutput.close();
        }
    }


    /**
     * Generates the Data Access Layer for each table.
     */
    public void generateDALs() {
        for (TableInfo table : setInfo.getTableInfos().values()) {
            generateDAL(table);
        }
    }

    private void writeSql(List<ColumnData> colData, List<String> params) throws IOException, GenException {
        StringBuilder columns = new StringBuilder("( ");
        StringBuilder values = new StringBuilder();

        String where = createPreparedWhereClause(params, false, colData);

        StringBuilder update = new StringBuilder();
        StringBuilder fields = new StringBuilder("    public static final class F{\n");
        StringBuilder fieldsAll = new StringBuilder("        public static String[] getAllFields() { return new String[] {");

        int i = 0;

        for (ColumnData cd : colData) {
            i++;
            boolean isNotLast = i < colData.size();
            String field = columnNameToConstantName(cd.getName());

            fields.append("        public static final String ").append(field).append(" = \"").append(cd.getName()).append("\";\n");
            fieldsAll.append(field);

            if (isNotLast) {
                fieldsAll.append(",");
            }

            if (!(config.useAutoincrement && cd.isAutoIncrement())) {
                if (cd.getName().equalsIgnoreCase(config.versionColName)) {
                    update.append(cd.getName()).append(" = ").append(cd.getName()).append("+1");
                } else {
                    update.append(cd.getName()).append("=?");
                }

                values.append("?");
                columns.append(cd.getName());

                if (isNotLast) {
                    update.append(",");
                    values.append(",");
                    columns.append(",");
                }
            }
        }

        values.append(" )\";");
        columns.append(" )");
        update.append("\"\n    ").append(where).append(";");
        fieldsAll.append("};}\n");

        out("protected String getDeleteSql(){\n    return \"" + "delete from \" + getTableName() + \" where " + StringUtils.join(params, "=? and ") + "=?" + "\";\n}\n");

        fields.append(fieldsAll);
        fields.append("    }\n");
        out(fields.toString());

        out("@Override\nprotected String getInsertSql(){\n    return \"insert into \" + getTableName() + \" " + columns
                + " values ( " + values + "\n}\n");

        out("@Override\nprotected String getUpdateSql(){\n    return \"update \" + getTableName() + \" set " + update + "\n}\n");

    }

    private void writeObjs(String tableName, List<String> params, List<ColumnData> columnData) throws IOException {

        out("@Override\nprotected Object[] getInsertObjs(" + this.getPojoClassName(tableName) + " info){\n    return new Object[]{"
                + makeInsertObjects(config.useAutoincrement, columnData, config.versionColName) + "};\n}\n");
        out("@Override\nprotected Object[] getUpdateObjs(" + this.getPojoClassName(tableName) + " info){\n    return new Object[]{"
                + makeUpdateObjects(params, columnData, config.useAutoincrement, config.versionColName) + "};\n}\n");

    }

    public void runV1(String filename, OutputStream out) {
        initConsole(out);

        if (filename != null && filename.length() > 0) {
            init(filename);
        }
        IDbHelper dbHelper = getDbHelper(dbConfig);
        try {
            makeDir();
            DatabaseMetaData meta = connect(dbHelper.getConnection());
            setInfo.getTableData(config, dbHelper, meta);
            setInfo.getOnFlyViewData(config, dbHelper);

            /*
             * 如果generator_all == yes，则生成所有表 否则，进行本地快照和数据库的比对
             */
            generateBaseList();
            if (config.generateAll) {

                generatePojos();
                generateDALs();

                generateBusinesses();

                generateWebUI();

                generateTest();
                if (config.jspDir != null) {
                    generateJsp();
                }
                generateSpringXml();

                if (config.generateProcedures) {
                    generateStoreProcedures(meta);
                }
            } else {
                IDbHelper dbHelper2 = getDbHelper(dbConfig);
                Connection conn = getDbHelper(dbConfig).getConnection();
                try {
                    DbDiff dd = new DbDiff(dbHelper, dbHelper2, getTableDataFromLocalCache(), conn, config.catalog, config.schema);
                    dd.addDiffHandler(this);
                    dd.compareTables(config.getR2dc());
                } finally {
                    DbUtils.close(conn);
                }
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace(console);
            LOG.error("", e);
        }

        info("Successfully complected!");
    }

    private void writeGetSetId(String tableName, List<String> keyData, List<ColumnData> columnData) throws IOException, GenException {
        String type = getPrimaryKeyType(keyData, columnData);
        out("@Override");
        out("public " + type + " getId(" + this.getPojoClassName(tableName) + " info) {");
        out("    return " + this.getOneId(keyData) + ";");
        out("}");
        out("\n");

        out("@Override");
        out("public void setId(" + this.getPojoClassName(tableName) + " info, " + type + " id) {");
        if (isNotEmpty(keyData)) {
            out("    info.set" + firstUp(keyData.get(0)) + "(id);");
        } else {
            out("   //no key to set, don't this method!");
        }
        out("}");
        out("\n");


        out("@Override");
        out("public void setIdLong(" + this.getPojoClassName(tableName) + " info, long id) {");
        if (isNotEmpty(keyData)) {
            out("    info.set" + firstUp(keyData.get(0)) + "(" + unboxType(type) + "id);");
        } else {
            out("   //no key to set, don't this method!");
        }
        out("}");
        out("\n");

    }

    private void writeDummyUpdate(String tableName, String methodName) throws IOException {
        out("");
        out("public boolean " + methodName + "(" + this.getPojoClassName(tableName) + " info) throws SQLException {");
        out("    throw new SQLException(\"This operation is not supported, because table " + tableName
                + " not have a unique key!\");");
        out("}");

    }

    /**
     * 生成单个dal
     *
     * @param table database table
     */
    private void generateDAL(TableInfo table) {

        StopWatch sw = new StopWatch();
        sw.start();
        try {

            String tableName = table.getName();
            info("Generating DALs : " + tableName);
            String baseDir = config.getBaseSrcDir();
            String fileName = baseDir + File.separator + "dal" + File.separator + getPojoClassName(tableName)
                    + "List.java";
            currentOutput = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(fileName), TableGenConfig.FILE_ENCODING), 10240);

            List<ColumnData> colData = table.getFields();

            // updates the keyData variable
            List<String> keyData = setInfo.getTableKeys(tableName);

            writeHeader(table, "dal", " extends " + genericFiles[0] + getGeneticType(table));

            // +
            // " implements RowMapper<"+getPojoClassName(tableName)+">, IBaseListService");

            // do the retrieve,insert and update functions
            // We have pair of updates and retrieve, depending
            // upon whether keys and/or indexes are present on
            // the database table.
            //
            // If no keys/indexes are present, then you'll just have to
            // write your own retrieve and update functions.
            //
            writeDALConstructor(tableName);

            writeSql(colData, keyData);

            writeObjs(tableName, keyData, colData);

            writeGetTableName(tableName, "getTableName", false);
            writeGetKeyName(getPrimaryKeyName(keyData));

            if(config.r2dbc) {
                out(mapRowR2dbc(colData, getPojoClassName(tableName)));
            }else {
                out(mapRow(colData, getPojoClassName(tableName)));
            }

            writeGetSetId(tableName, keyData, colData);
            writeGetNextKey(keyData, colData);

            if (isNotEmpty(keyData)) {

                /* optimistic lock update ! */
                if (containsColName(colData, config.versionColName)) {
                    boolean withVersion = true;
                    writeUpdate(tableName, keyData, "updateByKeyAndVersion", false, withVersion, colData);
                    writeUpdate(tableName, keyData, "updateByKeyAndVersion", true, withVersion, colData);
                }
                writeDelete(tableName, keyData, "deleteByKey", false, colData);
                writeDelete(tableName, keyData, "deleteByKey", true, colData);
            } else {
                writeDummyUpdate(tableName, "updateByKey");
                writeDummyDelete(tableName, keyData, "deleteByKey", colData);
            }

            sw.split();
            trace("after keys:" + sw.getSplitTime());

            //foreign key
            List<ForeignKeyDefinition> foreignKeyData = setInfo.getTableImportedKeys(tableName);
            for (ForeignKeyDefinition fkEnum : foreignKeyData) {
                writeImportedMethods(tableName, fkEnum, colData);
            }

            foreignKeyData = setInfo.getTableExportedKeys(tableName);
            for (ForeignKeyDefinition fkEnum : foreignKeyData) {
                writeExportedMethods(fkEnum, setInfo.getTableInfos().get(fkEnum.primaryKeyTableName).getFields());
            }


            sw.split();
            trace("foreignKeys :" + sw.getSplitTime());

            if (config.indexes) {
                Map<String, List<String>> indexData = setInfo.getTableIndexes(tableName);
                // variable
                for (String indexName : indexData.keySet()) {
                    List<String> cols = indexData.get(indexName);
                    String name = firstUp(indexName);
                    writeSelect(tableName, cols, "selectBy" + name, colData);
                    writeSelectAll(tableName, cols, false, "selectAllLike" + name, true, colData);

                    writeUpdate(tableName, cols, "updateBy" + name, false, false, colData);
                    writeUpdate(tableName, cols, "updateBy" + name, true, false, colData);

                    writeDelete(tableName, cols, "deleteBy" + name, false, colData);
                    writeDelete(tableName, cols, "deleteBy" + name, true, colData);
                    writeCount(cols, false, "countBy" + name, colData);
                    writeCount(cols, true, "countLike" + name, colData);
                }
            }

            writeInsertDummy(tableName, keyData, colData);
            out("}");

            currentOutput.close();

        } catch (SQLException | IOException | GenException e) {
            e.printStackTrace(console);
        }
        sw.split();
        trace("after close:" + sw.getSplitTime());
    }

    String getPojoClassName(String tableName) {
        String classname = tableName2ClassName(tableName);
        if (tableName.equals(classname)) {
            return config.addPrefix + firstUp(tableName);
        } else {
            return config.addPrefix + classname;
        }
    }

    private String tableName2ClassName(String tableName) {
        if (setInfo.getTableDataExt() != null && setInfo.getTableDataExt().get(tableName) != null) {
            String alias = setInfo.getTableDataExt().get(tableName).getClassname();

            LOG.debug("Pojo class alias is: " + alias);

            if (StringUtils.isNotEmpty(alias)) {
                return alias.trim();
            }
        }
        return tableName;
    }

    private void writeDummyDelete(String tableName, List<String> keyData2, String methodName, List<ColumnData> columnData) throws IOException, GenException {
        out("");
        out("public boolean " + methodName + "(" + getPrimaryKeyType(keyData2, columnData) + " key) throws SQLException {");
        out("    throw new SQLException(\"This operation is not supported, because table " + tableName
                + " not have a unique key!\");");
        out("}");

    }

    /**
     * Generates the plane java model for each table.
     */
    public void generateBusinesses() {
        for (TableInfo table : setInfo.getTableInfos().values()) {
            generateBusiness(table);
        }
    }

    public void generateBusiness(TableInfo table) {
        String tableName = table.getName();
        info("Generating business : " + tableName);
        String baseDir = config.getBaseSrcDir();
        String fileName = baseDir + File.separator + BUSINESS + File.separator + getPojoClassName(tableName)
                + MANAGER_SUFFIX + ".java";
        File f = new File(fileName);
        boolean fileExists = f.exists();
        if (!fileExists) {

            try {
                currentOutput = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(fileName), TableGenConfig.FILE_ENCODING));

                writeHeader(table, BUSINESS, " extends " + getPojoClassName(tableName) + "List");
                out("    /**\n" +
                        "     * @param ds datasource for injecting\n" +
                        "     */\n" +
                        "    public " + getPojoClassName(tableName) + MANAGER_SUFFIX + "(DataSource ds) {\n" +
                        "        super(ds);\n" +
                        "    }");

                out("}");
                currentOutput.close();
            } catch (IOException e) {
                e.printStackTrace(console);
                LOG.warn("", e);
            }

        } else {
            info("File " + fileName + " allready exists! Skip rewrite.");
        }

    }

    private String formFieldsString(List<ColumnData> columnData) {
        return
                columnData.stream()
                        .map(ColumnData::getName)
                        .map(it -> "\"" + it + "\"")
                        .reduce((a, b) -> a + "," + b)
                        .get()
                        .toLowerCase()
                ;

    }

    /**
     * Generates the plane java model for each table.
     */
    public void generateWebUI() throws SQLException {
        try {
            for (TableInfo table : setInfo.getTableInfos().values()) {
                String tableName = table.getName();
                info("Generating web UI : " + tableName);
                String baseDir = config.getBaseSrcDir();
                String fileName = baseDir + File.separator + "web" + File.separator + getPojoClassName(tableName)
                        + "WebUI.java";
                File f = new File(fileName);
                boolean fileExists = f.exists();
                if (config.overWriteAll || !fileExists) {
                    currentOutput = new BufferedWriter(
                            new OutputStreamWriter(new FileOutputStream(fileName), TableGenConfig.FILE_ENCODING));

                    List<ColumnData> colData = table.getFields();
                    List<String> keyData = setInfo.getTableKeys(tableName);
                    writeHeader(table, "web", " extends AbstractWebUI" + getGeneticType(table));
                    out("");
                    out("");
                    out("protected String getUniquePropertyName(){return \""
                            + (isNotEmpty(keyData) ? keyData.get(0).toLowerCase() : "null") + "\";}");

                    // getColNames
                    out("");
                    out("@Override");
                    out("protected String[] getColNames() {");
                    out("\t\treturn new String[] {\"chkbox\"," + formFieldsString(colData) + "};");
                    out("}");
                    out("");

                    // getKeys
                    out("\t@Override");
                    if (isNotEmpty(keyData)) {
                        String keyType = getPrimaryKeyType(keyData, colData);
                        out("\tprotected " + keyType + "[] getKeys(HttpServletRequest request) {");
                        out("\t\tString[] res = request.getParameterValues(checkboxName);");
                        out("\t\tif(res==null) return new " + keyType + "[0];");
                        out("\t\treturn (" + keyType + "[]) converter.convert(\n"
                                + "                request.getParameterValues(checkboxName), " + keyType + ".class);");
                        out("\t}");
                        out("");
                    } else {
                        out("\tprotected Object[] getKeys(HttpServletRequest request) {    \n"
                                + "\t\treturn request.getParameterValues(checkboxName);\n" + "\t}");

                    }

                    // setService
                    out(" public void setService(" + this.getPojoClassName(tableName) + "Manager service) {");
                    out("        this.service = service;");
                    out("    }");

                    // validateColumn
                    out("/**");
                    out("* An example of how to validate the worksheet column cells.");
                    out("*");
                    out("* protected void validateColumn(WorksheetColumn worksheetColumn, String changedValue) {");
                    out("*    if (changedValue.equals(\"foo\")) {");
                    out("*        worksheetColumn.setErrorKey(\"foo.error\");");

                    out("*    } else {");
                    out("*        worksheetColumn.removeError();");
                    out("*    }");
                    out("* } ");
                    out("*/");

                    out("}");
                } else {
                    info("File " + fileName + " allready exists! Skip rewrite.");
                }
                currentOutput.close();
            }

        } catch (IOException e) {
            e.printStackTrace(console);
        } catch (GenException e) {
            e.printStackTrace();
        }
    }

    void writeBaseHeader() throws IOException {
        out("package " + config.packageName + ";");
        out("/*");
        out("  * BaseList");
        writeHeaderComment();
        out("*/");
        out("  import com.bixuebihui.BeanFactory;");
        out("  import com.bixuebihui.jdbc.BaseDao;");
        out("  import com.bixuebihui.jdbc.IDbHelper;");
        out("  import com.bixuebihui.jdbc.MSDbHelper;");
        out("  import com.bixuebihui.jdbc.aop.DbHelperAroundAdvice;");
        out("  import org.springframework.aop.framework.ProxyFactory;");

        out("  import javax.sql.DataSource;");
        out("  import java.sql.SQLException;");
        out("");

        out("public abstract class BaseList<T,V> extends BaseDao<T,V>");
        out("{");
    }

    /**
     * Generates the container/database access functions for each table.
     */
    void writeHeader(TableInfo table, String subPackage, String extClass) throws IOException {
        out("package " + config.packageName + "." + subPackage + ";");
        out("/*");
        out("  * " + table.getName() + (table.getComment() != null ? (": " +  table.getComment() ) : ""));
        writeHeaderComment();
        out("  */");
        out("");

        out("import java.sql.*;");

        if ("pojo".equals(subPackage)) {
            out("import java.io.Serializable;");
        }

        if ("pojo".equals(subPackage) && config.useAnnotation) {
            out("import jakarta.validation.constraints.*;");
            out("import org.apache.commons.text.StringEscapeUtils;");
            if (config.useSwagger) {
                out("import io.swagger.annotations.ApiModel;");
                out("import io.swagger.annotations.ApiModelProperty;");
                out("\n");
                out("@ApiModel(description = \"" + table.getComment() + "\")");
            }
        }

        if (!"pojo".equals(subPackage)) {

            out("import java.util.List;");
            out("import javax.sql.DataSource;");

            if (!"dal".equals(subPackage) && !"web".equals(subPackage)) {
                out("import " + config.packageName + ".dal.*;");
            } else {
                out("import " + config.packageName + ".business.*;");
            }
            out("import " + config.packageName + ".pojo.*;");

            if ("dal".equals(subPackage) || "stub".equals(subPackage)) {
                out("import com.bixuebihui.jdbc.RowMapperResultReader;");
            }

            if ("stub".equals(subPackage)) {
                out("import org.apache.commons.dbutils.DbUtils;");
            }

            if (extClass.contains("TestCase")) {
                out("import junit.framework.TestCase;");
            }

            if ("web".equals(subPackage)) {
                out("import " + config.packageName + ".business." + this.getPojoClassName(table.getName()) + "Manager;");

                out("import org.jmesa.worksheet.WorksheetColumn;");
                out("importjakarta.servlet.http.HttpServletRequest;");

                out("import org.apache.commons.beanutils.ConvertUtilsBean;");
            }

            for (String genericFile : genericFiles) {
                out("import " + config.packageName + "." + genericFile + ";");
            }

            if ("web".equals(subPackage)) {
                out("import com.bixuebihui.jmesa.AbstractWebUI;");
            }
            out("");
        }

        extClass = extClass == null ? "" : extClass;
        if (config.useAnnotation && BUSINESS.equals(subPackage) && !extClass.contains("TestCase")) {
            out("import org.springframework.stereotype.Repository;");
            out("");
            out("@Repository");

        }

        if ("dal".equals(subPackage)) {
            out("public class " + getPojoClassName(table.getName())
                    + "List"
                    + ((extClass.contains("TestCase")) ? "Test" : "") + " " + (extClass));
        } else {
            out("public class " + getPojoClassName(table.getName())
                    + (BUSINESS.equals(subPackage) ? "Manager" : ("web".equals(subPackage) ? "WebUI" : ""))
                    + ((extClass.contains("TestCase")) ? "Test" : "") + " " + (extClass));
        }
        out("{");
    }

    private String getBusFullClassName(String tableName) {
        return config.packageName + ".business." + this.getPojoClassName(tableName) + "Manager";
    }

    private void writeHeaderComment() throws IOException {
        out("  * ");
        out("  * Notice! Automatically generated file!");
        out("  * Do not edit the pojo and dal packages,use `maven tablegen:gen`!");
        out("  * Code Generator originally by J.A.Carter");
        out("  * Modified by Xing Wanxiang 2008-2021");
        out("  * email: www@qsn.so");
    }

    void writeInit(ColumnData cd) throws IOException {
        String name = firstLow(cd.getName());
        String type = cd.getJavaType();
        String code = typeDefaultValue.get(type);

        if ("String".equals(type) && !cd.isNullable()) {
            code = "\"*\"";
        }

        if (code != null) {
            out(INDENT + name + "=" + code + ";");
        }
    }

    /**
     * Writes out the variables with javadoc info.
     */
    void writeVariable(String tableName, ColumnData cd) throws IOException {
        out("/**");
        out("  * " + cd);
        out("  * " + getColumnDescription(tableName, cd));
        out("  */");
        out(getColumnAnnotation(tableName, cd));
        out("  protected " + cd.getJavaType() + " " + CaseUtils.toCamelCase(cd.getName(), false, '_') + ";");
        out("");
    }

    String getColumnAnnotation(String tableName, ColumnData cd) {
        return TableUtils.getColumnAnnotation(config, setInfo, tableName, cd, true);
    }

    /**
     * Writes out the set and get functions for each variable.
     */
    void writeSetGet(String tableName, ColumnData cd) throws IOException {
        String name = firstLow(cd.getName());
        String newName = firstUp(cd.getName());

        out("/**");
        out("  * Sets the value for " + name + " " + getColumnDescription(tableName, cd));
        out("  */");
        out("public void set" + newName + "(" + cd.getJavaType() + " " + name + ")");
        out("{");
        out("  this." + name + "=" + name + ";");
        out("}");
        out("");
        out("/**");
        out("  * Gets the value for " + name);
        out("  */");
        out("public " + cd.getJavaType() + " get" + newName + "()");
        out("{");
        if ("Timestamp".equals(cd.getJavaType())) {
            out("  return " + name + "==null ? null: new Timestamp(" + name + ".getTime());");
        } else if ("Date".equals(cd.getJavaType())) {
            out("  return " + name + "==null ? null: new Date(" + name + ".getTime());");
        } else {
            out("  return " + name + ";");
        }

        out("}");
        out("");
    }

    /**
     * Writes out the retrieveAll function. This retrieves all the rows that
     * match the search pattern.
     */
    void writeSelectAll(String tableName, List<String> params, boolean customWhere, String methodName, boolean bLike, List<ColumnData> columnData)
            throws IOException {
        String query = "select * from \" + getTableName() + \" \"";
        String where = createPreparedWhereClause(params, bLike, columnData);
        String objs = createPreparedObjects(params, bLike, columnData);

        out("/**");
        out("  * Select from the database for table \"" + firstUp(tableName) + "\"");
        out("  */");

        if (customWhere) {
            out("public List<" + getPojoClassName(tableName) + "> " + methodName
                    + "(String where) throws SQLException");
        } else {
            out(createMethodLine(methodName, params, "List<" + getPojoClassName(tableName) + ">", columnData));
        }

        out("{");
        out("    String query = \"" + query +"\""+ where+"\"" + (customWhere ? "+ where;" : ";"));
        out("    return dbHelper.executeQuery(query, " + (objs == null ? "null" : objs) + ", new RowMapperResultReader<"
                + getPojoClassName(tableName) + ">(this));");
        out("}");
        out("");
    }

    /**
     * Writes out the Select function.
     */
    void writeSelect(String tableName, List<String> params, String methodName, List<ColumnData> columnData) throws IOException, GenException {
        String query = "select * from \" + getTableName() + \" \"";
        String where = createPreparedWhereClause(params, false, columnData);
        String objs = createPreparedObjects(params, false, columnData);

        out("/**");
        out("  * Select from the database for table \"" + tableName + "\"");
        out(" */");
        out(createMethodLine(methodName, params, getPojoClassName(tableName), columnData));
        out("{");
        out("    String query = \"" + query +"\""+ where + "\";");
        out("    List<" + getPojoClassName(tableName) + "> info = dbHelper.executeQuery(query, " + objs
                + ", new RowMapperResultReader<" + getPojoClassName(tableName) + ">(this));");
        out("    if(info!=null && info.size()>0) {");
        out("         return  info.get(0);");
        out("    }");
        out(" return null;");
        out("}");
        out("");
    }


    void writeSelectPageTest(String tableName, String methodName) throws IOException {

        out("/**");
        out("  * Select from the database for table \"" + tableName + "\"");
        out(" */");
        out("public void test" + firstUp(methodName) + "() throws SQLException");
        out("{");
        String className = this.getPojoClassName(tableName) + "Manager";
        out("  " + className + " man = new " + className + "();");

        out("    List<" + getPojoClassName(tableName) + "> list = man." + methodName + "(\"\",\"\", 0, 10);");
        out("    for(" + getPojoClassName(tableName) + " info: list){");
        out("     System.out.println(info.toXml());");
        out("    }");
        out("}");
        out("");
    }

    private String getGeneticType(TableInfo table) throws GenException, SQLException {
        String tableName = table.getName();
        List<String> keyData = setInfo.getTableKeys(tableName);
        List<ColumnData> columnData = table.getFields();
        return "<" + getPojoClassName(tableName) + "," + getPrimaryKeyType(keyData, columnData) + ">";
    }


    /**
     * Writes out the Select function.
     */
    void writeDelete(String tableName, List<String> params, String methodName, boolean isWithConn, List<ColumnData> columnData)
            throws IOException, GenException {
        String keyType;
        StringBuilder tmp = new StringBuilder("public boolean " + methodName + "(");

        String objs = this.createPreparedObjects(params, false, columnData);

        // work out the query string and the method parameters.
        Iterator<String> iterator = params.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            keyType = getColType(key, columnData);
            tmp.append(keyType).append(" ").append(key.toLowerCase());
            if (iterator.hasNext()) {
                tmp.append(",");
            } else {
                tmp.append((isWithConn ? ", Connection cn" : "") + ") throws SQLException");
            }
        }

        out("/**");
        out("  * Deletes from the database for table \"" + tableName + "\"");
        out("  */");
        out(tmp.toString());
        out("{");
        out("");
        out("    return 1 <= dbHelper.executeNoQuery(getDeleteSql()," + objs + (isWithConn ? ", cn" : "") + ");");
        out("}");
        out("");
    }


    /**
     * Writes out the dummy insert (blank record) function.
     *
     * @param keyData
     */
    void writeInsertDummy(String tableName, List<String> keyData, List<ColumnData> columnData) throws IOException {

        out("/**");
        out("  * Inserts the dummy record of " + this.getPojoClassName(tableName)
                + " object values into the database.");
        out("  */");
        out("@Override");
        out("public boolean insertDummy() throws SQLException");
        out("{");
        out("     " + this.getPojoClassName(tableName) + "  info = new " + this.getPojoClassName(tableName) + "();");

        boolean isFirst = true;
        for (ColumnData columnDatum : columnData) {
            if (!columnDatum.isNullable() &&
                    "String".equals(columnDatum.getJavaType())) {
                if (isFirst) {
                    out("     java.util.Random rnd = new java.util.Random();");
                    isFirst = false;
                }
                out("    info.set" + firstUp(columnDatum.getName())
                        + "(Integer.toString(Math.abs(rnd.nextInt(Integer.MAX_VALUE)), 36));");
            }
        }

        if (isNotEmpty(keyData)) {
            out("    info.set" + firstUp(keyData.get(0)) + "(getNextKey());");
        }
        out("    return this.insert(info);");

        out("}");
        out("");
    }


    /**
     * Writes out the count function. If "key" is not null then a search based
     * on the key is added. If "withLike" then the search is based on a "like"
     * not an exact match.
     */
    void writeDALConstructor(String tableName) throws IOException {
        String classPojo = this.getPojoClassName(tableName);
        String className = classPojo + "List";

        out("/**");
        out("  * Don't direct use the " + className + ", use " + classPojo + "Manager instead.");
        out("  */");
        out("protected " + className + "(DataSource ds)");
        out("{");
        out("    super(ds);");
        out("}");
        out("");
    }

    /**
     * Writes out the update function.
     */
    void writeUpdate(String tableName, List<String> params, String methodName, boolean isWithConn,
                     boolean isWithVersion, List<ColumnData> columnData) throws IOException {
        // work out the "where" part of the update first..
        String paramObjs = createKeyObjects(params)
                + (isWithVersion ? ",info.get" + firstUp(config.versionColName) + "() " : "");

        out("/**");
        out("  * Updates the current object values into the database with version condition as an optimistic database lock.");
        out("  */");
        out("public boolean " + methodName + "(" + this.getPojoClassName(tableName) + " info"
                + (isWithConn ? ", Connection cn" : "") + ") throws SQLException");
        out("{");
        out("    String updateSql = getUpdateSql()" + (isWithVersion ? "" : "+\" and version=?\"") + ";");

        StringBuilder objs = new StringBuilder();
        for (ColumnData cd : columnData) {
            boolean added = true;
            if (isWithVersion && cd.getName().equalsIgnoreCase(config.versionColName)) {
                added = false;
            } else {
                objs.append(" info.get").append(firstUp(cd.getName())).append("()");
            }
            if (added) {
                objs.append(",");
            }
        }

        out("    return 1 == dbHelper.executeNoQuery(updateSql, new Object[]{" + objs + "\n\t " + paramObjs + "}"
                + (isWithConn ? ", cn" : "") + ");");

        out("}");
        out("");

    }

    private String getOneId(List<String> keyData2) {
        if (isNotEmpty(keyData2)) {
            String key = getPrimaryKeyName(keyData2);
            if (key != null) {
                return " info.get" + firstUp(key) + "()";
            }
        }
        return "0L";
    }

    /**
     * Writes out the count function. If "key" is not null then a search based
     * on the key is added. If "withLike" then the search is based on a "like"
     * not an exact match.
     */
    void writeCount(List<String> params, boolean withLike, String methodName, List<ColumnData> columnData) throws IOException, GenException {
        String where;

        where = createPreparedWhereClause(params, withLike, columnData);
        String objs = this.createPreparedObjects(params, withLike, columnData);

        out("/**");
        out("  * Counts the number of entries for this table in the database.");
        out("  */");
        out(createMethodLine(methodName, params, "int", columnData));
        out("{");
        out("    String query=\"select count(*) from \" + getTableName() + \"  " + where + "\";");
        out("    Object o = dbHelper.executeScalar(query," + objs + ");");
        out("  return o==null?0:Integer.parseInt(o.toString());");

        out("}");
        out("");
    }

    void writeGetTableName(String tableOrKeyName, String methodName, boolean isKey) throws IOException {
        out("/**");
        out("  * Get " + (isKey ? "key" : "table") + " name.");
        out("  */");
        out("@Override");
        out("public String " + methodName + "()");
        out("{");
        if (tableOrKeyName == null) {
            out("    return " + "\"\"" + ";");
        } else {
            out("    return " + (isKey ?
                    "F." + columnNameToConstantName(tableOrKeyName)
                    : "\"" + tableOrKeyName + "\""
            ) + ";");
        }
        out("}");
        out("");
    }

    void writeGetKeyName(String keyName) throws IOException {
        writeGetTableName(keyName, "getKeyName", true);
    }

    void writeCountWhereTest(String tableName) throws IOException {

        out("public void test" + firstUp("count") + "() throws SQLException");
        out("{");
        String className = this.getPojoClassName(tableName) + "Manager";
        out("  " + className + " man = new " + className + "();");
        out("    assertTrue(man." + "count" + "(\"\")>=0);");
        out("}");
        out("");
    }

    /**
     * since c-dbtools 0.7.1, only generate getNextKey for Timestamp and String/UUID
     *
     * @param keyData db key info
     * @throws IOException io error
     */
    void writeGetNextKey(List<String> keyData, List<ColumnData> columnData) throws IOException, GenException {

        String type = getPrimaryKeyType(keyData, columnData);

        if ("Timestamp".equals(type) || "String".equals(type)) {
            out("public " + type + " getNextKey(){\n");
            if (keyData != null && keyData.size() == 1) {
                if ("Timestamp".equals(type)) {
                    out("\treturn new Timestamp(System.currentTimeMillis());");
                } else {
                    out("\treturn java.util.UUID.randomUUID().toString();");
                }

            } else if (keyData != null && keyData.size() > 1) {
                out("\tthrow new IllegalStateException(\"联合主键时，无法生成自增长主键\");");
            } else {//没有主键
                out("\treturn System.currentTimeMillis();");
            }
            out("}\n");
            out("\n");
        }

    }

    public String createPreparedObjects(List<String> params, boolean withLike, List<ColumnData> columnData) {
        StringBuilder objs = new StringBuilder();

        // if we have keys passed in then we add a "where" to the count
        // e.g. se
        //
        if (params != null) {
            // work out the "where" part of the update first..
            //
            objs.append(" new Object[]{ ");
            String key;
            String keyType;
            for (Iterator<String> e = params.iterator(); e.hasNext(); ) {
                key = e.next();
                keyType = getColType(key, columnData);

                // only allow likes on String types...
                // Hmmm should we allow more than this???
                //
                if ((withLike) && ("String".equals(keyType))) {
                    objs.append("\"%\"+").append(key.toLowerCase()).append("+\"%\"");
                } else {
                    objs.append(key.toLowerCase());
                }

                if (e.hasNext()) {
                    objs.append(" , ");
                }
            }

            objs.append("}");
        } else {
            objs = null;
        }

        return objs == null ? null : objs.toString();
    }

    /**
     * Creates a the parameter block for a method.
     */
    public String createMethodLine(String methodName, List<String> params, String returnType, List<ColumnData> columnData) {
        StringBuilder paramString = new StringBuilder("public " + returnType + " " + methodName + "(");
        String key;
        String keyType;

        if (params != null) {
            // work out the query string and the method parameters.
            //
            for (Iterator<String> e = params.iterator(); e.hasNext(); ) {
                key = e.next();
                keyType = getColType(key, columnData);
                if (UNKNOWN_TYPE.equals(keyType)) {
                    LOG.error("unsupported future when generate method " + methodName + " return type " + returnType);
                }
                paramString.append(keyType).append(" ").append(key.toLowerCase());

                if (e.hasNext()) {
                    paramString.append(",");
                } else {
                    paramString.append(") throws SQLException");
                }
            }
        } else {
            paramString.append(") throws SQLException");
        }

        return paramString.toString();
    }


    /**
     * write the methods associated to this Exported Foreign key entry.
     */
    protected void writeExportedMethods(ForeignKeyDefinition def, List<ColumnData> columnData) throws IOException, GenException {
        out("/**");
        out("  * Get all related  " + def.getFKTableName() + " which have same " + def.getFKColList());
        out("  */");

        out(createMethodLine("getRelated" + firstUp(def.getFKTableName()) + "__" + firstUp(def.getFKColumnName()), def.getPKFields(),
                "List<" + this.getPojoClassName(def.getFKTableName()) + "> ", columnData));
        out("{");
        out("\t" + firstUp(def.getFKTableName()) + "Manager x = new " + firstUp(def.getFKTableName()) + "Manager(this.getDbHelper().getDataSource());");
        out("\treturn x.selectBy" + def.getFKTableName() + "__" + def.getFKColumnName() + "(" + def.getPKColList() + ");");
        out("}");
    }

    /**
     * write the methods associated to this Foreign key entry.
     */
    protected void writeImportedMethods(String tableName, ForeignKeyDefinition def, List<ColumnData> columnData) throws IOException, GenException {
        out("/**");
        out("  * Imported " + tableName + " PK:" + def.getPKTableName() + " FK:" + def.getFKTableName());
        out("  */");
        out(createMethodLine("get" + firstUp(def.getPKTableName()) + "By" + def.getFKColumnName(), def.getFKFields(),
                firstUp(def.getPKTableName()), columnData));
        out("{");
        out("\t" + firstUp(def.getPKTableName()) + "Manager x = new " + firstUp(def.getPKTableName()) + "Manager(this.getDbHelper().getDataSource());");
        out("\treturn x.selectByKey(" + def.getFKColList() + ");");
        out("}");
        writeSelectAll(tableName, def.getFKFields(), false,
                "selectBy" + def.getFKTableName() + "__" + def.getFKColumnName(),
                false, columnData);
    }


    /**
     * Wrapper for text output. So we can change where the output goes easily!
     */
    public void out(String text) throws IOException {
        currentOutput.write(text + "\n");
    }

    @Override
    public void processTableDiff(String tableName) throws IOException {

        if (config.excludeTablesList != null && config.excludeTablesList.containsKey(tableName)) {
            return;
        }

        if (!CollectionUtils.isEmpty(config.tablesList) && !config.tablesList.containsKey(tableName)) {
            return;
        }

        TableInfo table = setInfo.getTableInfos().get(tableName);

        generatePojo(table);
        generateDAL(table);
        generateBusiness(table);
    }

    @Override
    public void processEnd(HashMap<String, List<ColumnData>> tableData) {//NOSONAR
        if (generateSuccess) {
            saveTableDataToLocalCache(tableData);
        }
    }

}
