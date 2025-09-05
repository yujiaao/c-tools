package com.bixuebihui.tablegen.generator;

import com.bixuebihui.tablegen.GenException;
import com.bixuebihui.tablegen.TableGen;
import com.bixuebihui.tablegen.entry.ColumnData;
import com.github.jknack.handlebars.Handlebars;
import org.apache.commons.io.output.StringBuilderWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.bixuebihui.db.KeywordsUtils.*;
import static com.bixuebihui.tablegen.NameUtils.columnNameToConstantName;
import static com.bixuebihui.tablegen.NameUtils.firstUp;
import static com.bixuebihui.tablegen.TableGen.INDENT;
import static java.util.Collections.emptyList;

/**
 * @author xwx
 */
public class DalGenerator extends BaseGenerator {
    protected final static String CLASS_SUFFIX = "List";


    public static String makeInsertObjects(boolean useAutoincrement, List<ColumnData> columnData, String versionColName) {
        return makeInsertObjects(false, emptyList(), useAutoincrement, false, columnData, versionColName);
    }

    private static String makeInsertObjects(boolean skipKey, List<String> keys,
                                            boolean useAutoincrement, boolean skipVersionColumn,
                                            List<ColumnData> columnData, String versionColName) {
        StringBuilder objs = new StringBuilder();
        boolean useVersion = skipVersionColumn && containsColName(columnData, versionColName);
        Iterator<ColumnData> iterator = columnData.iterator();
        while (iterator.hasNext()) {
            ColumnData cd = iterator.next();
            if (useAutoincrement && cd.isAutoIncrement()
                    || (useVersion && cd.getName().equalsIgnoreCase(versionColName))
                    || !isInsertAndUpdatableField(cd, useAutoincrement)
            ) {
                continue;
            }
            if (skipKey && keys.contains(cd.getName())) {
                continue;
            }

            objs.append("info.get").append(firstUp(cd.getName())).append("()");

            if (iterator.hasNext()) {
                objs.append(",");
            }
        }
        if (objs.toString().contains(",") && objs.lastIndexOf(",") == objs.length() - 1) {
            objs.deleteCharAt(objs.length() - 1);
        }
        return objs.toString();
    }

    public static String makeUpdateObjects(List<String> params, List<ColumnData> columnData, boolean use_autoincrement, String versionColName) {
        boolean skipKey = true;
        return makeInsertObjects(skipKey, params, use_autoincrement, true, columnData, versionColName) + "," +
                createKeyObjects(params);
    }

    public static String makeUpdateObjectsWithExcludeFields(List<String> keys, List<ColumnData> columnData,
                                                            boolean use_autoincrement, String versionColName) {
        StringBuilder objs = new StringBuilder();
        boolean useVersion = containsColName(columnData, versionColName);
        /**
         * {{#each fields ~}}
         *         if(!containsIgnoreCase(F.{{constantName this.name}}, localExcludeFields)){
         *             list.add(info.get{{firstUp this.name}}());
         *         }
         *         {{/each~}}
         */
        for (ColumnData cd : columnData) {
            if (use_autoincrement && cd.isAutoIncrement()
                    || (useVersion && cd.getName().equalsIgnoreCase(versionColName))
                    || !isInsertAndUpdatableField(cd, use_autoincrement)
            ) {
                continue;
            }
            if (keys.contains(cd.getName())) {
                continue;
            }

            objs.append("    if(!containsIgnoreCase(F.").append(columnNameToConstantName(cd.getName())).append(", localExcludeFields)){\n")
                    .append("        list.add(info.get").append(firstUp(cd.getName())).append("());\n")
                    .append("    }\n");

        }

         return objs+
               keys.stream().map(key -> "  list.add(info.get"+firstUp(key)+"());\n").collect(Collectors.joining());
    }


    public static String createKeyObjects(List<String> params) {
        return getParamGattersString(params);
    }

    static String getParamGattersString(List<String> params) {
        StringBuilder objs = new StringBuilder();
        if (params != null) {
            String key;
            for (Iterator<String> e = params.iterator(); e.hasNext(); ) {
                key = e.next();

                objs.append("info.get").append(firstUp(key)).append("()");

                if (e.hasNext()) {
                    objs.append(" , ");
                }
            }

        } else {
            objs = null;
        }

        return objs == null ? null : objs.toString();
    }

    /**
     * Writes out the mapRow method. The mapRow method interprets the returned
     * result set from a Select and update the object with those values.
     */
    public static String mapRow(List<ColumnData> columnData, String pojoClassName) {
        String col;
        String colType;

        StringBuilder sb = new StringBuilder();
        sb.append(INDENT).append("/**\n")
                .append(INDENT).append("  * Updates the object from a selected ResultSet.\n")
                .append(INDENT).append("  */\n")
                .append(INDENT).append("@Override\n")
                .append(INDENT).append("public ").append(pojoClassName).append(" mapRow (ResultSet r, int index, Set<String> fields) throws SQLException\n")
                .append(INDENT).append("{\n")
                .append(INDENT).append(INDENT).append(pojoClassName).append(" res = new ").append(pojoClassName).append("();\n");

        for (ColumnData cd : columnData) {
            col = cd.getName();
            colType = cd.getJavaType();

            // work out which data type we are getting for each variable
            String ucol = "F." + columnNameToConstantName(col);

            String get;
            if (colType.compareTo("String") == 0) {
                get = "(r.getString(" + ucol + "));";
            } else if (colType.compareTo("byte") == 0 || colType.compareTo("Byte") == 0) {
                get = "(r.getByte(" + ucol + "));";
            } else if (colType.compareTo("long") == 0 || colType.compareTo("Long") == 0) {
                get = "(r.getLong(" + ucol + "));";
            } else if (colType.compareTo("int") == 0 || colType.compareTo("Integer") == 0) {
                get = "(r.getInt(" + ucol + "));";
            } else if (colType.compareTo("short") == 0 || colType.compareTo("Short") == 0) {
                get = "(r.getShort(" + ucol + "));";
            } else if (colType.compareTo("float") == 0 || colType.compareTo("Float") == 0) {
                get = "(r.getFloat(" + ucol + "));";
            } else if (colType.compareTo("double") == 0 || colType.compareTo("Double") == 0) {
                get = "(r.getDouble(" + ucol + "));";
            } else if (colType.compareTo("Date") == 0) {
                get = "(r.getDate(" + ucol + "));";
            } else if (colType.compareTo("Timestamp") == 0) {
                get = "(r.getTimestamp(" + ucol + "));";
            } else if (colType.compareTo("Time") == 0) {
                get = "(r.getTime(" + ucol + "));";
            } else if (colType.compareTo("char") == 0 || colType.compareTo("Char") == 0) {
                get = "(r.getString(" + ucol + ").charAt(0);";
            } else if (colType.compareTo("byte[]") == 0) {
                get = "(r.getBytes(" + ucol + "));";
            } else if (colType.compareTo("Boolean") == 0) {
                get = "(r.getBoolean(" + ucol + "));";
            } else if (colType.compareTo("Byte[]") == 0) {
                get = "(org.apache.commons.lang.ArrayUtils.toObject(r.getBytes(" + ucol + ")));";
            } else if (colType.compareTo("com.bixuebihui.jdbc.ClobString") == 0) {
                get = "(new com.bixuebihui.jdbc.ClobString(com.bixuebihui.jdbc.JDBCUtils.oracleClob2Str((Clob)r.getObject("
                        + ucol + "))));";
            } else {
                get = "(r.getObject(" + ucol + "));";
                LOG.error("Warning! Unknown type : " + colType + " in write mapRow");
            }

            get = firstUp(cd.getName()) + get;

            sb.append(INDENT).append(INDENT).append("if (containsIgnoreCase(" + ucol + ", fields)){\n");
            sb.append(INDENT).append(INDENT).append(INDENT).append("res.set" + get).append("\n");
            sb.append(INDENT).append(INDENT).append("}\n");

        }

        sb.append(INDENT).append(INDENT).append("return res;\n");
        sb.append(INDENT).append("}\n");
        return sb.toString();
    }

    /**
     * Writes out the mapRow method. The mapRow method interprets the returned
     * result set from a Select and update the object with those values.
     */
    public static String mapRowR2dbc(List<ColumnData> columnData, String pojoClassName) {
        String col;
        String colType;
        List<String> gets = new ArrayList<>();
        String get;

        for (ColumnData cd : columnData) {
            col = cd.getName();
            colType = cd.getJavaType();

            // work out which data type we are getting for each variable
            String ucol = "F." + columnNameToConstantName(col);

            if (colType.compareTo("String") == 0) {
                get = "(r.get(" + ucol + ", String.class));";
            } else if (colType.compareTo("byte") == 0 || colType.compareTo("Byte") == 0) {
                get = "(r.get(" + ucol + ", Byte.class));";
            } else if (colType.compareTo("long") == 0 || colType.compareTo("Long") == 0) {
                get = "(r.get(" + ucol + ", Long.class));";
            } else if (colType.compareTo("int") == 0 || colType.compareTo("Integer") == 0) {
                get = "(r.get(" + ucol + ", Integer.class));";
            } else if (colType.compareTo("short") == 0 || colType.compareTo("Short") == 0) {
                get = "(r.get(" + ucol + ", Short.class));";
            } else if (colType.compareTo("float") == 0 || colType.compareTo("Float") == 0) {
                get = "(r.get(" + ucol + ", Float.class));";
            } else if (colType.compareTo("double") == 0 || colType.compareTo("Double") == 0) {
                get = "(r.get(" + ucol + ", Double.class));";
            } else if (colType.compareTo("LocalDate") == 0 || colType.compareTo("Date") == 0) {
                get = "(r.get(" + ucol + ", LocalDate.class));";
            } else if (colType.compareTo("Timestamp") == 0 || colType.compareTo("LocalDateTime") == 0) {
                get = "(r.get(" + ucol + ", LocalDateTime.class));";
            } else if (colType.compareTo("LocalTime") == 0 || colType.compareTo("Time") == 0) {
                get = "(r.get(" + ucol + ", LocalTime.class));";
            } else if (colType.compareTo("char") == 0 || colType.compareTo("Char") == 0) {
                get = "(r.get(" + ucol + ", String.class).charAt(0);";
            } else if (colType.compareTo("byte[]") == 0) {
                get = "(r.get(" + ucol + ", byte[].class));";
            } else if (colType.compareTo("Boolean") == 0) {
                get = "(r.get(" + ucol + ", Boolean.class));";
            } else if (colType.compareTo("Byte[]") == 0) {
                get = "(org.apache.commons.lang.ArrayUtils.toObject(r.get(" + ucol + ")));";
            } else if (colType.compareTo("com.bixuebihui.jdbc.ClobString") == 0) {
                get = "(new com.bixuebihui.jdbc.ClobString(com.bixuebihui.jdbc.JDBCUtils.oracleClob2Str((Clob)r.get("
                        + ucol + " ))));";
            } else {

                get = "(r.getObject(" + ucol + "));";
                LOG.error("Warning! Unknown type : " + colType + " in write mapRow");
            }

            get = firstUp(cd.getName()) + get;
            gets.add(get);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(INDENT).append("/**\n")
                .append(INDENT).append("  * Updates the object from a selected ResultSet.\n")
                .append(INDENT).append("  */\n")
                .append(INDENT).append("@Override\n")
                .append(INDENT).append("public " + pojoClassName + " mapRow(Row r) \n")
                .append(INDENT).append("{\n")
                .append(INDENT).append(INDENT).append(pojoClassName + " res = new " + pojoClassName + "();\n");

        for (String e : gets) {
            sb.append(INDENT).append(INDENT).append("res.set" + e).append("\n");
        }

        sb.append(INDENT).append(INDENT).append("return res;\n");
        sb.append(INDENT).append("}\n");
        return sb.toString();
    }

    @Override
    String getTemplateFileName(String tableName) {
        return "dal.java";
    }

    @Override
    protected Map<String, Object> getContextMap(String tableName) {
        Map<String, Object> v = super.getContextMap(tableName);
        List<ColumnData> cols = getFieldsList(tableName);
        List<String> keys = getKeysInfo(cols, tableName);

        v.put("hasVersionCol", containsColName(cols
                , config.getVersionColName()));
        v.put("hasLogicDelCol", containsColName(cols
                , config.getDelColName()));


        v.put("insertObjects", makeInsertObjects(config.isUseAutoincrement(), cols, config.getVersionColName()));
        v.put("updateObjects", makeUpdateObjects(keys, cols, config.isUseAutoincrement(), config.getVersionColName()));
        v.put("updateObjectsWithExcludeFields", makeUpdateObjectsWithExcludeFields(keys, cols, config.isUseAutoincrement(), config.getVersionColName()));
        v.put("insertSql", makeInsertSql(config.isUseAutoincrement(), cols));
        v.put("updateSql", makeUpdateSql(keys, cols, config.isUseAutoincrement(), config.getVersionColName()));


        v.put("firstKeyType", this.getPrimaryKeyType(tableName));
        v.put("firstKeyName", this.getFirstKeyName(tableName));
        v.put("firstKeyFirstColumnName", this.getFirstKeyFirstColumnName(tableName));
        v.put("firstKeyFirstColumnType", this.getPrimaryKeyFirstColumnType(tableName));

        v.put("whereNoLike", TableGen.createPreparedWhereClause(keys,
                false, cols));
        v.put("keyObjects", createKeyObjects(keys));
        v.put("keys", keys);


        v.put("isView", isView());
        v.put("mapRow", mapRow(cols, this.getPojoClassName(tableName)));
        if (config.getR2dc()) {
            v.put("mapRow", mapRowR2dbc(cols, this.getPojoClassName(tableName)));
        } else {
            v.put("mapRow", mapRow(cols, this.getPojoClassName(tableName)));
        }
        v.put("indexWriter", this.getIndexWriter(tableName));
        return v;
    }


    protected String getIndexWriter(String tableName) {

        try (StringBuilderWriter writer = new StringBuilderWriter()) {

            TableGen tableGen = new TableGen(System.out);
            tableGen.setCurrentOutput(writer);
            tableGen.setConfig(this.config);
            tableGen.setSetInfo(this.setInfo);
            tableGen.writeForIndex(tableName, getFieldsList(tableName));

            return writer.toString();
        } catch (GenException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Object makeUpdateSql(List<String> keys, List<ColumnData> cols, boolean useAutoincrement, String versionColName) {
        return
                "update \"+this.getTableName()+\" set \"\n    +\"" +
                        cols.stream().filter(c -> !keys.contains(c.getName())
                                        && isInsertAndUpdatableField(c, useAutoincrement))
                                .map(columnData -> {

                                            String columnName = decorFieldIfIsKeyword(columnData.getName(), this.getDbType());

                                            return columnData.getName().equals(versionColName) ? columnName + "=" + columnName + "+1"
                                                    : columnName + "=?";
                                        }
                                ).collect(Collectors.joining(","));

    }


    private String makeInsertSql(boolean useAutoincrement, List<ColumnData> cols) {
        return "insert into \"+this.getTableName()+\n\" ( " +
                cols.stream().filter(it -> isInsertAndUpdatableField(it, useAutoincrement))
                        .map(it -> decorFieldIfIsKeyword(it.getName(), this.getDbType())).collect(Collectors.joining(", "))
                + ")\"\n    +\" values (\"\n    +\""
                + cols.stream().filter(it -> isInsertAndUpdatableField(it, useAutoincrement))
                .map(column -> "?").collect(Collectors.joining(",")) + ")";
    }

    @Override
    String getClassSuffix() {
        return CLASS_SUFFIX;
    }

    @Override
    public String getTargetFileName(String tableName) {
        return getTargetFileName("dal", tableName);
    }

    @Override
    String getClassName(String tableName) {
        return getPojoClassName(tableName) + CLASS_SUFFIX;
    }

    @Override
    protected void additionalSetting(Handlebars handlebars) {
        super.additionalSetting(handlebars);
        // usage: {{type tableName colName}}
        handlebars.registerHelper("colType", (tableName, options) -> getColType(options.param(0), this.setInfo.getTableCols((String) tableName)));
        handlebars.registerHelper("isKey", (tableName, options) -> this.setInfo.getTableKeys((String) tableName).contains(options.param(0)));

        // usage: {{where tableName}}
        handlebars.registerHelper("where", (tableName, options) ->
        {
            boolean like = options.param(0);
            return TableGen.createPreparedWhereClause(this.setInfo.getTableKeys((String) tableName), like, this.setInfo.getTableCols((String) tableName));
        });


    }



}
