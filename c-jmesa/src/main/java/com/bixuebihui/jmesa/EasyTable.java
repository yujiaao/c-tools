package com.bixuebihui.jmesa;

import com.bixuebihui.DbException;
import com.bixuebihui.jdbc.BaseDao;
import com.bixuebihui.jdbc.IDbHelper;
import com.bixuebihui.jmesa.entry.Query;
import com.bixuebihui.jmesa.mock.SimpleHttpServletRequest;
import com.bixuebihui.jmesa.mock.SimpleHttpServletResponse;
import com.bixuebihui.util.DbUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foundationdb.sql.StandardException;
import com.foundationdb.sql.parser.*;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.values.ValuesStatement;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * For more easy use with jmesa
 *
 * @author xwx
 */
public class EasyTable extends BasicWebUI {

    /**
     * 用来解析sql并保存解析结果
     */
    MiniSqlParser msp;

    private boolean onePage = true;

    public EasyTable(IDbHelper dbhelper, String tableCaption, String baseSql) {
        this(dbhelper, tableCaption, baseSql, null,null, null);
    }

    public EasyTable(IDbHelper dbhelper, String tableCaption, String baseSql, Object[] parameters) {
        this(dbhelper, tableCaption, baseSql, parameters,null, null);
    }

    /**
     * @deprecated use constructor with  colNames
     */
    @Deprecated
    public EasyTable(IDbHelper dbhelper, String tableCaption, String baseSql, Object[] parameters, @Nullable String pkName, @Nullable String tableId) {
        this(dbhelper, tableCaption, baseSql,parameters, pkName,tableId, null);
    }

        /**
         * use com.foundationdb.sql.parser.SQLParser to detect columns and key
         * 通过数据库返回的元数据确定列名
         *
         * @param dbhelper     use as data source
         * @param tableCaption 表名描述
         * @param baseSql      核心sql语句
         * @param tableId      表名
         * @param pkName       主键名
         */
    public EasyTable(IDbHelper dbhelper, String tableCaption, String baseSql, Object[] sqlParameters,
                     @Nullable String pkName, @Nullable String tableId, String colNames) {
        this.id = tableId;
        super.setTableCaption(tableCaption);

        init(dbhelper, baseSql, sqlParameters);

        if (pkName != null) {
            this.setUniquePropertyName(pkName);
        }

        setColsList(colNames);
        if(colNames==null) {
            msp = initMeta(baseSql);
        }
        if (pkName != null && msp != null) {
            this.setUniquePropertyName(msp.uniquePropertyName);
        }

        if (this.id == null && msp != null && msp.tableName != null) {
            this.setId(msp.tableName);
        } else {
            this.setId(tableCaption);
        }
    }

    public EasyTable() {

    }

    protected void init(IDbHelper dbHelper, String baseSql, Object[] params) {
        this.service = new BasicListService(dbHelper);
        if (isOnePage()) {
            this.maxRows = 500;
            this.maxRowsIncrements[0] = maxRows;
            LOG.debug("maxRows=" + maxRows + "  maxRowsIncrements[0]=" + this.maxRowsIncrements[0]);
        }
        ((BasicListService) this.service).setCoreSql(baseSql);
        ((BasicListService) this.service).setSqlParams(params);
    }


    protected MiniSqlParser initMeta(String baseSql) {
        try {
            return MiniSqlParser.parse(baseSql);
        } catch (RuntimeException | JSQLParserException e) {
            LOG.warn("sql can't be parsed:" + baseSql);
            LOG.warn(e.getMessage());
        }
        return null;
    }

    @Override
    protected String[] getColNames() {
        if(msp!=null) {
            return msp.colNames;
        }else{
            String cols =  getColsList();
            if(cols!=null) {
                return cols.split(",");
            }else {
                LOG.error("you must set colNames or colsList");
            }
            return new String[0];
        }
    }

    public String[] getColLabelsFromDb(String sql) throws SQLException {
        try(Connection cn = service.getDbHelper().getConnection()) {
            return MiniSqlParser.getByDb(cn, sql).colNames;
        }
    }

    @Override
    public String getColsList() {
        if(msp!=null){
            return StringUtils.join(msp.colNames, ",");
        }
        return super.getColsList();
    }

    /**
     * use LimitActionFactoryJsonImpl
     * request json format:
     *     { "id": "table-id", "action": "", "maxRows": 500, "page": 123,
     * "filter": { "property1":value1, "property2":value2, "property3": [fromValue, toValue]},

     *     "sort":{ "property1":"asc", "property2":"desc", }, "exportType":"json", }
     * response:
     *    {
     * "caption":"t_config",
     * "titles": [
     * "C _key",
     * "c_name",
     * "c_value"],
     * "data": [{
     * "c_key":"key1",
     * "c_name":"name1",
     * "c_value":"value1"}
     * ],
     *
     * "paging":{"page":2,"maxRows":1,"rowEnd":2,"rowStart":1,"totalRows":3}}
     */
    public String json(Map<String, Object> paramsMap) throws SQLException {
        SimpleHttpServletRequest request = new SimpleHttpServletRequest();
        if(paramsMap==null || paramsMap.isEmpty()) {
            request.setParameter(this.id + "_e_", "json");
        }else {
            request.setAttribute(JSON_QUERY, paramsMap);
        }
        return getJsonData(request);
    }

    /**
     * use @see LimitActionFactoryJsonImpl
     * json format { "id": "table-id", "action": "", "maxRows": 500, "page": 123,     *
     *     "filter": :[{"key":"id","comparison":"between","value":[1,30000]}],
     *     * "sort":{ "property1":"asc", "property2":"desc", },
     * "exportType":"json", }
     */
    public String json(String jsonQuery) throws SQLException {
        SimpleHttpServletRequest request = new SimpleHttpServletRequest();
        if(StringUtils.isBlank(jsonQuery)) {
            request.setParameter(this.id + "_e_", "json");
        }else {
            request.setAttribute(JSON_QUERY, jsonQuery);
        }
        return getJsonData(request);
    }

    private String getJsonData(SimpleHttpServletRequest request) throws SQLException {
        SimpleHttpServletResponse response = new SimpleHttpServletResponse();
        response.setCharacterEncoding(Charset.defaultCharset().displayName());

        this.handleRequestInternal(request, response);

        request.getAttribute(tableCaption);

        try {
            return response.getContentAsString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getId() {
        return id;
    }

    public boolean isOnePage() {
        return onePage;
    }

    public void setOnePage(boolean onePage) {
        this.onePage = onePage;
    }

    public String json(Query jsonQuery) throws SQLException {
            return render(jsonQuery);
    }


    public static class MiniSqlParser {
        public String[] colNames;
        public String[] colLabels;
        public String tableName;
        public String uniquePropertyName;
        public String[] colTypes;

        public static MiniSqlParser parseOld(String sql) throws StandardException {
            MiniSqlParser sp = new MiniSqlParser();

            //TODO may be change to use https://github.com/JSQLParser/JSqlParser

            SQLParser parser = new SQLParser();

            StatementNode stmt = parser.parseStatement(sql);
            if (stmt instanceof CursorNode) {
                CursorNode cn = (CursorNode) stmt;

                ResultSetNode rn = cn.getResultSetNode();
                ResultColumnList cl = rn.getResultColumns();

                String[] tmpColNames = cl.getColumnNames();

                sp.colLabels = new String[cl.size()];

                sp.colNames = tmpColNames;
                if (sp.colNames != null && sp.colNames.length > 0) {
                    for (int i = 0; i < cl.size() && tmpColNames.length > i && sp.colNames[i] != null; i++) {
                        sp.colLabels[i] = cl.get(i).getExpression().toString();
                    }
                }
                if (rn instanceof SelectNode) {
                    SelectNode sn = (SelectNode) rn;
                    FromList fromList = sn.getFromList();

                    sp.tableName = fromList.get(0).getExposedName();

                } else {
                    LOG.debug("rn = " + rn);
                }

            } else {
                LOG.warn("not a select!");
            }

            assert sp.colNames != null;
            if (sp.colNames.length > 0) {
                sp.uniquePropertyName = sp.colNames[0];
            }

            return sp;
        }

        public static MiniSqlParser parse(String sql) throws JSQLParserException {
            MiniSqlParser sp = new MiniSqlParser();

            //TODO may be change to use https://github.com/JSQLParser/JSqlParser

            List<String> labelList = new ArrayList<>();
            List<String> nameList = new ArrayList<>();

            Select select = (Select) CCJSqlParserUtil.parse(sql);


            SelectVisitor sv = new SelectVisitor(){
                @Override
                public void visit(PlainSelect plainSelect) {

                     plainSelect.getFromItem().accept(new    FromItemVisitor (){
                         @Override
                         public void visit(Table tableName) {
                             if(sp.tableName==null) {
                                 sp.tableName = tableName.getName();
                             }
                         }

                         @Override
                         public void visit(SubSelect subSelect) {
                         }

                         @Override
                         public void visit(SubJoin subjoin) {
                         }

                         @Override
                         public void visit(LateralSubSelect lateralSubSelect) {
                         }

                         @Override
                         public void visit(ValuesList valuesList) {
                         }

                         @Override
                         public void visit(TableFunction tableFunction) {
                         }

                         @Override
                         public void visit(ParenthesisFromItem aThis) {

                         }
                     });

                    plainSelect.getSelectItems().forEach(it -> {
                        SelectItemVisitor sv1 = new SelectItemVisitor(){
                            @Override
                            public void visit(AllColumns allColumns) {
                            }

                            @Override
                            public void visit(AllTableColumns allTableColumns) {
                            }

                            @Override
                            public void visit(SelectExpressionItem selectExpressionItem) {

                                if(selectExpressionItem.getAlias()!=null) {
                                    String name = selectExpressionItem.getAlias().getName();
                                    labelList.add(name);
                                    nameList.add(name);
                                }else {
                                    String name =  selectExpressionItem.getExpression().toString();
                                    if(name.contains(".")){
                                        name = name.substring(name.indexOf(".")+1);
                                    }
                                    labelList.add(name);
                                    nameList.add(name);
                                }

                            }
                        };
                        it.accept(sv1);
                    });
                }

                @Override
                public void visit(SetOperationList setOperationList) {
                }

                @Override
                public void visit(WithItem withItem) {
                }

                @Override
                public void visit(ValuesStatement valuesStatement) {
                }
            };
            select.getSelectBody().accept(sv);

            sp.colLabels = labelList.toArray(new String[labelList.size()]);
            sp.colNames = nameList.toArray(new String[nameList.size()]);



            if (sp.colNames.length > 0) {
                sp.uniquePropertyName = sp.colNames[0];
            }

            return sp;
        }

        public static MiniSqlParser getByDb(Connection cn, String sql)
                throws DbException {
            MiniSqlParser sp = new MiniSqlParser();
            Statement stmt = null;
            ResultSet rs = null;
            ResultSetMetaData rsmd;
            try {
                String sqlWithLimit = BaseDao.getPagingSql(cn.getMetaData().getDriverName(), sql, 0,0);

                stmt = cn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_READ_ONLY);

                rs = stmt.executeQuery(sqlWithLimit);

                rsmd = rs.getMetaData();
                sp.colNames = new String[rsmd.getColumnCount()];
                sp.colLabels = new String[rsmd.getColumnCount()];
                sp.colTypes = new String[rsmd.getColumnCount()];
                sp.tableName = rsmd.getTableName(1);

                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    sp.colNames[i - 1] = rsmd.getColumnName(i);
                    sp.colLabels[i - 1] = rsmd.getColumnLabel(i);
                    sp.colTypes[i-1] = rsmd.getColumnTypeName(i);
                }
            }catch (SQLException e){
                throw new DbException(e);
            }finally {
                try {
                    DbUtils.close(rs);
                    DbUtils.close(stmt);
                }catch (Exception e){}
            }

            return sp;
        }

    }

}
