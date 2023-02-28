package com.bixuebihui.tablegen.dbinfo;

import com.bixuebihui.tablegen.entry.ColumnData;
import org.apache.commons.lang3.StringUtils;

import java.sql.DatabaseMetaData;
import java.util.List;

import static com.bixuebihui.tablegen.NameUtils.firstUp;

/**
 * @author xwx
 */
public class ProcedureGen {


    /**
     * The execute method returns a boolean to indicate the form of the first result. You must call either the method getResultSet or getUpdateCount to retrieve the result; you must call getMoreResults to move to any subsequent result(s).
     * <p>
     * Returns:
     * true if the first result is a ResultSet object; false if the first result is an update count or there is no result
     *
     * @param proc
     * @param params
     * @return
     */
    public static String process(ProcedureInfo proc, List<ProcedureParameterInfo> params) {
        return "public <T> " + getReturnType(proc) + " "
                + normalizeArgName(proc.getSimpleName()) + "("
                + getArgs(params) + (params.size() > 0 ? "," : "") + " RowMapperResultReader<T> reader) throws SQLException {\n"
                + getCallSql(proc.getSimpleName(), params.size())
                + getStatement()
                + getParameterSets(params)
                + "    boolean isReturnResultSet = cstmt.execute();\n"
                + "    if(isReturnResultSet && reader !=null){\n"
                + "			rs = cstmt.getResultSet();\n"
                + "        List<T> res = reader.processResultSet(rs);\n"
                + "        return res.size();\n"
                + "    }else{\n"
                + "        return cstmt.getUpdateCount();\n"
                + "    }\n"
                + getCloseAll()
                + "\n}";
    }


    private static String getParameterSets(List<ProcedureParameterInfo> params) {
        StringBuilder sb = new StringBuilder();
        int i = 1;
        for (ProcedureParameterInfo info : params) {

            String type = getArgType(info);
            if ("java.math.BigDecimal".equals(type)) {
                type = "BigDecimal";
            } else if ("byte[]".equals(type)) {
                type = "Bytes";
            } else {
                type = firstUp(type);
            }
            sb.append("    cstmt.set" + type + "(" + i + "," + normalizeArgName(info.getSimpleName()) + ");\n");
            i++;
        }
        return sb.toString();
    }


    private static String getStatement() {
        return "	Connection con = getDbHelper().getConnection();\n" +
                "	CallableStatement cstmt = null;\n" +
                "	ResultSet rs = null;\n" +
                "	try {\n" +
                "		cstmt = con.prepareCall(sql);\n";
    }

    private static String getCloseAll() {
        return "    } finally {\n" +
                "			DbUtils.closeQuietly(con, cstmt, rs);\n" +
                "	}";
    }


    protected static String getReturnType(ProcedureInfo proc) {
        String res = "";
        switch (proc.getProcedureType()) {
            case DatabaseMetaData.procedureNoResult:
                res = "void";
                break;
            case DatabaseMetaData.procedureResultUnknown:
                res = "ResultSet";
                break;
            case DatabaseMetaData.procedureReturnsResult:
            default:
                res = "int";
                break;
        }
        return res;

    }

    protected static String getArgType(ProcedureParameterInfo param) {
        ColumnData cd = new ColumnData(param.getSimpleName(), param.getType_name(), (int) param.getLength(), param.getNullable());
        return cd.getJavaType();
    }

    protected static String normalizeProcName(String simpleName) {
        if (simpleName.indexOf(";") > 0) {
			simpleName = simpleName.substring(0, simpleName.indexOf(";"));
		}
        return simpleName;
    }


    protected static String normalizeArgName(String simpleName) {
        simpleName = simpleName.replaceAll("@", "_").replace("#", "_");

        simpleName = normalizeProcName(simpleName);

        if (StringUtils.isNumericSpace(simpleName)) {
            simpleName = "_" + simpleName;
            simpleName = simpleName.replaceAll(" ", "");
        }
        return simpleName;
    }

    protected static String getCallSql(String procName, int paramSize) {
        return "    String sql = \"{call " + normalizeProcName(procName) + "(" + StringUtils.repeat("?", ",", paramSize) + ")}\";\n";
    }

    protected static String getArgs(List<ProcedureParameterInfo> params) {
        StringBuilder sb = new StringBuilder();
        if (params != null && params.size() > 0) {
            for (ProcedureParameterInfo param : params) {
                sb.append(",").append(getArgType(param)).append(" ").append(normalizeArgName(param.getSimpleName()));
            }
            return sb.deleteCharAt(0).toString();
        }
        return "";
    }
}
