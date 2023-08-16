package com.bixuebihui.jmesa;


import com.bixuebihui.cache.DictionaryCache;
import com.bixuebihui.jdbc.*;
import com.bixuebihui.jmesa.entry.Query;
import com.bixuebihui.jsp.TimeSpan;
import com.bixuebihui.util.ParameterUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.jmesa.limit.*;
import org.jmesa.model.ExportTypes;
import org.jmesa.model.TableModel;
import org.jmesa.model.TableModelUtils;
import org.jmesa.model.WorksheetSaver;
import org.jmesa.view.component.Column;
import org.jmesa.view.component.Row;
import org.jmesa.view.component.Table;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.html.component.HtmlColumn;
import org.jmesa.worksheet.UniqueProperty;
import org.jmesa.worksheet.Worksheet;
import org.jmesa.worksheet.WorksheetColumn;
import org.jmesa.worksheet.WorksheetUtils;
import org.jmesa.worksheet.editor.CheckboxWorksheetEditor;
import org.jmesa.worksheet.editor.WorksheetCheckboxHeaderEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author xwx
 */
public abstract class AbstractWebUI<T, V> implements WorksheetSaver {

    public static final String VAR_NAME = "row";
    public static final String CHECK_BOX_ID = "chkbox";
    public static final String SAVE_WORKSHEET_ACTION = "save_worksheet";
    public static final String ADD_WORKSHEET_ROW_ACTION = "add_worksheet_row";
    private static final String TABLE_I18N = "tables";
    protected static ConvertUtilsBean converter = new ConvertUtilsBean();
    protected static Logger LOG = LoggerFactory.getLogger(BasicWebUI.class);
    /**
     * The unique table id.
      */
    protected String id;

    protected String successView;
    protected IBaseListService<T, V> service;
    protected String checkboxName = "chk";
    protected String actionParam = "ac";
    protected String editableParam = "editable";
    protected Map<String, String> colsTemplate;
    protected String tableCaption = null;

    public AbstractWebUI() {
    }

    /**
     * A very custom way to filter the items. The PresidentFilter acts as a
     * command for the Hibernate criteria object. There are probably many ways
     * to do this, but this is the most flexible way I have found. The point is
     * you need to somehow take the Limit information and filter the rows.
     *
     * @param limit The Limit to use.
     */
    public static SqlFilter getFilter(Limit limit) {
        try {
            return getFilter(limit, null);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new SqlFilter();
    }

    public static SqlFilter getFilter(SqlFilter sqlFilter, Limit limit, String tableAlias) throws ParseException {
        FilterSet filterSet = limit.getFilterSet();
        return getFilter(sqlFilter, filterSet, tableAlias);
    }

    public static SqlFilter getFilter(SqlFilter sqlFilter, FilterSet filterSet, String tableAlias) throws ParseException {
        if(filterSet==null){
            return sqlFilter;
        }

        Collection<Filter> filters = filterSet.getFilters();

        for (Filter filter : filters) {
            String property = filter.getProperty();
            Object[] value = filter.getValue();
            String prop = tableAlias==null? property : tableAlias + "." + property;
            if (TimeSpan.isTimeSpan(value.toString())) {
                TimeSpan ts = TimeSpan.build(value.toString());
                sqlFilter.between(prop, ts.getBeginDate(), ts.getEndDate());
            } else if (NumberRange.isNumberRange(value.toString())){
                NumberRange numberRange = NumberRange.build(value.toString());
                sqlFilter.between(prop, numberRange.getBegin(), numberRange.getEnd());
            }else {
                sqlFilter.addFilter(prop, SqlFilter.Comparison.valueOf(filter.getComparison().toString()), value);
            }
        }

        if(!filterSet.getFilterSets().isEmpty()) {
            List<SqlFilter> subFilters = new ArrayList<>();
            for (FilterSet set : filterSet.getFilterSets()) {
                SqlFilter subSqlFilter = new SqlFilter();
                SqlFilter subFilter = getFilter(subSqlFilter, set, tableAlias);
                subFilters.add(subFilter);
            }
            sqlFilter.addSubGroupCondition(SqlFilter.Operator.valueOf(String.valueOf(filterSet.getOperator())),
                    subFilters.toArray(new SqlFilter[0]));
        }
        return sqlFilter;
    }

    public static SqlFilter getFilter(Limit limit, String tableAlias) throws ParseException {
        SqlFilter sqlFilter = new SqlFilter();
        return getFilter(sqlFilter, limit, tableAlias);
    }

    /**
     * A very custom way to sort the items. The PresidentSort acts as a command
     * for the Hibernate criteria object. There are probably many ways to do
     * this, but this is the most flexible way I have found. The point is you
     * need to somehow take the Limit information and sort the rows.
     *
     * @param limit The Limit to use.
     */
    public static SqlSort getSort(Limit limit) {
        SqlSort sqlSort = new SqlSort();
        SortSet sortSet = limit.getSortSet();
        Collection<Sort> sorts = sortSet.getSorts();
        for (Sort sort : sorts) {
            String property = sort.getProperty();
            String order = sort.getOrder().toParam();
            sqlSort.addSort(property, order);
        }
        return sqlSort;
    }

    protected abstract String render(Query query);

    /**
     * 取表列名
     * @return
     */
    protected abstract String[] getColNames();

    public void setTitles(org.jmesa.view.component.Table table,
                          String tableName, String[] colNames, boolean skipFirst) {

        table.setCaption(this.getTableCaption(tableName));
            ResourceBundle bundle =null;
            try {
                bundle = ResourceBundle.getBundle(TABLE_I18N);
            } catch (Exception e){
                LOG.info(e.getMessage());
            }
        int start = skipFirst ? 1 : 0;
        for (int i = start; i < colNames.length; i++) {
            String key = colNames[i];
            if(bundle!=null) {
                try {
                    key = bundle.getString(tableName + "." + colNames[i]);
                } catch (Exception e) {
                    LOG.info(e.getMessage());
                }
            }
            table.getRow().getColumn(colNames[i]).setTitle(key);
        }
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setService(IBaseListService<T, V> service) {
        this.service = service;
    }

    /**
     * 返回选中记录的主键id
     * @param request  HttpServletRequest
     * @return id数组
     */
    protected abstract V[] getKeys(HttpServletRequest request);

    protected String render(HttpServletRequest request,
                            HttpServletResponse response) {
        TableModel tableModel = new TableModel(id, request, response);

        String ac = request.getParameter(actionParam);
        LOG.debug("AbstractWebUI " + actionParam + " = " + ac);

        return baseRender(tableModel,
                "true".equals(request.getParameter(editableParam)), ac,
                getKeys(request));
    }



    protected String render(Map<String, Object> request,
                            Map<String, Object> response) {
        TableModel tableModel = new TableModel(id, request, response);

        String ac = request.get(actionParam).toString();
        LOG.debug("AbstractWebUI " + actionParam + " = " + ac);

        return baseRender(tableModel,
                "true".equals(request.get(editableParam)), ac,
                (V[]) getKeys(request));
    }

    Long[] getKeys(Map<String, Object> request) {
        return Stream.of(request.get(checkboxName).toString().split(","))
                .map(Long::parseLong).toArray(Long[]::new);
    }


    protected void onSetCustomToolBar(CustomToolbar toolbar) {
        toolbar.addButton(CustomToolbar.RUN);
    }

    protected void onRun(TableModel tableModel, V[] keys) {

    }

    protected String baseRender(TableModel tableModel, boolean editable,
                                String action, V[] keys)  {
        tableModel.setEditable(editable);
        this.performAction(action, tableModel, keys);

        Table table = tableModel.isExporting() ? TableModelUtils
                .createTable(getColNames()) : TableModelUtils
                .createHtmlTable(getColNames());

        // Tell the tableFacade what exports to use.
        tableModel.setExportTypes(ExportTypes.CSV, ExportTypes.EXCEL);
        tableModel.setStateAttr("restore");
        // Find the data to display and
        setDataAndLimitVariables(tableModel);
        // build the Limit.
        CustomToolbar toolbar = new CustomToolbar();

        toolbar.enablePageNumbers(true);

        //allow user add additional custom tool bar buttons
        onSetCustomToolBar(toolbar);
        tableModel.setToolbar(toolbar);


        // 表名，字段名等保存在tables_zh_CN.properties的文件中
        this.setTitles(table, id, getColNames(), true);
        final Row row = table.getRow();
        row.setUniqueProperty(getUniquePropertyName());

        tableModel.setTable(table);

        if (tableModel.isExporting()) {
            exportRender(tableModel);
            // Will write the export data out to the response.
            // In Spring return null tells the controller not to do
            // anything.
            return null;
        } else {
            HtmlColumn chkbox = (HtmlColumn) row.getColumn(CHECK_BOX_ID);
            chkbox.setHeaderEditor(new WorksheetCheckboxHeaderEditor());

            chkbox.setCellEditor((item, property, rowcount) -> {

                UniqueProperty rowid = row.getUniqueProperty(item);
                String v = rowid == null ? "no_unique_property" : rowid.getValue();

                return "<input name=\"" + checkboxName
                        + "\" id='chk_" + rowcount
                        + "' type=\"checkbox\" value=\""
                        + v
                        + "\">";
            });

            chkbox.setTitle(" ");
            chkbox.setFilterable(false);
            chkbox.setSortable(false);
            chkbox.setEditable(false);
            return tableModel.render();
        }
    }

    protected Object exportRender(TableModel tableModel) {
        if (ExportTypes.EXCEL.equals(tableModel.getExportType())) {
            tableModel.setExportFileName(tableCaption + ".xls");
        }

        LOG.debug("Exporting - " + tableModel.getExportType() + ": " + tableCaption + ".xls");
        return tableModel.render();
    }

    public String getTableCaption(String tableName) {
        if (tableCaption == null) {
            ResourceBundle bundle = ResourceBundle.getBundle(TABLE_I18N);
            try {
                tableCaption = bundle.getString(tableName);
                return tableCaption;
            } catch (Exception e) {
                LOG.warn(e.getMessage());
                return tableName;
            }
        }
        return tableCaption;
    }

    public void setTableCaption(String tableCaption) {
        this.tableCaption = tableCaption;
    }

    /**
     * 返回主键键名
     * @return  键名
     */
    protected abstract String getUniquePropertyName();

    /**
     * user must override this method for use like below:
     * if (changedValue.equals("foo")) {
     * worksheetColumn.setErrorKey("foo.error");
     * } else {
     * worksheetColumn.removeError();
     * }
     */
    protected void validateColumn(WorksheetColumn worksheetColumn,
                                  String changedValue) {
        LOG.debug(" NO custom validateColumn method is implemented");
    }

    public String handleRequestInternal(HttpServletRequest request,
                                        HttpServletResponse response) {
        String mv = successView;
        String html = render(request, response);
        if (html == null) {
            // an export
            return null;
        } else {
            request.setAttribute(this.id, html);
            // Set the Html in the
            // request for the JSP.
        }
        return mv;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    protected Object convert(String value, Class<?> clazz) {
        if (clazz == ClobString.class) {
            return new ClobString(value);
        }

        return ConvertUtils.convert(value, clazz);

    }

    protected void performAction(String action, TableModel tableModel, V[] keys)
             {

        if (SAVE_WORKSHEET_ACTION.equals(action)) {
            tableModel.saveWorksheet(this);
        } else if (CustomToolbar.EDIT.equals(action)) {

        } else if (ADD_WORKSHEET_ROW_ACTION.equals(action)
                || CustomToolbar.INSERT.equals(action)) {
            service.insertDummy();
        } else if (CustomToolbar.DELETE.equals(action)) {
            deleteSelected(keys);
        } else if (CustomToolbar.COPY.equals(action)) {
            copySelected(keys);
        } else if (CustomToolbar.RUN.equals(action)) {
            onRun(tableModel, keys);
        }
    }

    protected void deleteSelected(V[] keys) {
        if (keys == null) {
            return;
        }
        for (V key : keys) {
            if (!service.deleteByKey(key)) {
                LOG.warn("fail to delete key=" + key);
            }
        }
    }

    protected void copySelected(V[] parameterValues) {
        if (parameterValues == null) {
            return;
        }
        java.util.ArrayList<String> list = new java.util.ArrayList<>();
        for (V parameterValue : parameterValues) {
            list.add(parameterValue.toString());
        }
        Map<String, T> infos = service.selectByIds(getUniquePropertyName(),
                list);
        for (T info : infos.values()) {
            if (!service.insertAutoNewKey(info)) {
                LOG.warn("fail to insert info=" + info.toString());
            }
        }
    }

    /**
     * An example of how to save the worksheet.
     */
    @Override
    public void saveWorksheet(Worksheet worksheet) {
        if (!worksheet.isSaving() || !worksheet.hasChanges()) {
            return;
        }
        String uniquePropertyName = WorksheetUtils
                .getUniquePropertyName(worksheet);
        List<String> uniquePropertyValues = WorksheetUtils
                .getUniquePropertyValues(worksheet);
        final Map<String, T> infos;
        infos = service.selectByIds(uniquePropertyName,
                uniquePropertyValues);
        worksheet.processRows(worksheetRow -> {
            Collection<WorksheetColumn> columns = worksheetRow.getColumns();
            for (WorksheetColumn worksheetColumn : columns) {
                String changedValue = worksheetColumn.getChangedValue();
                validateColumn(worksheetColumn, changedValue);
                if (worksheetColumn.hasError()) {
                    continue;
                }
                String uniqueValue = worksheetRow.getUniqueProperty()
                        .getValue();
                T info = infos.get(uniqueValue);
                boolean isNew = false;
                if (info == null) {
                    info = service.create();
                    isNew = true;
                }
                String property = worksheetColumn.getProperty();
                try {
                    if ("selected".equals(worksheetColumn.getProperty())) {
                        if (changedValue
                                .equals(CheckboxWorksheetEditor.CHECKED)) {
                            PropertyUtils.setProperty(info, property, "y");
                        } else {
                            PropertyUtils.setProperty(info, property, "n");
                        }
                    } else {
                        Object value = convert(changedValue, PropertyUtils
                                .getPropertyType(info, property));

                        PropertyUtils.setProperty(info, property, value);
                    }
                } catch (Exception e) {
                    LOG.error("", e);
                    throw new JMesaException(
                            "Not able to set the property [" + property
                                    + "] when saving worksheet.");
                }
                boolean res = isNew ? service.insert(info) : service.updateByKey(info);
                if (!res) {
                    LOG.warn("fail to update info=" + info.toString());
                }
            }
        });
    }


    protected void setDataAndLimitVariables(TableModel tableModel) {
        tableModel.setItems(new ItemsProvider<>(this.getUniquePropertyName(), service));
    }

    public Map<String, String> getColsTemplate() {
        return colsTemplate;
    }

    public void setColsTemplate(Map<String, String> colsTemplate) {
        this.colsTemplate = colsTemplate;
    }

    protected Map<String, Object> getContext(Map<String, Object> request) {
        return request;
    }
    protected Map<String, Object> getContext(HttpServletRequest request) {
        if (request == null) {
            return Collections.emptyMap();
        }
        Map<String, Object> context = new HashMap<>(16);
        context.putAll(request.getParameterMap());
        Enumeration<?> en = request.getAttributeNames();
        while (en.hasMoreElements()) {
            String key = (String) en.nextElement();
            context.put(key, request.getAttribute(key));
        }
        if (request.getSession() != null) {
            HttpSession session = request.getSession();
            Enumeration<?> sen = session.getAttributeNames();
            while (sen.hasMoreElements()) {
                String key = (String) sen.nextElement();
                context.put(key, session.getAttribute(key));
            }
        }
        return context;
    }

    protected void prepareCellEditor(Column col, Map<String, ?> context) {

        String var = VAR_NAME;

        String template = getColsTemplate() == null ? "" : getColsTemplate().get(col.getProperty());

        if (StringUtils.isBlank(template)) {
            return;
        }

        //replace %{abc} to ${abc}, to avoid spring xml escaping
        template = template.replaceAll("%\\{(.+?)}", "\\$\\{$1\\}");

        LOG.debug("after replace " + var + ":" + template);
        CellEditor cellEditor = getCellEditor(context, col, var, template);

        col.setCellEditor(cellEditor);
    }

    /**
     * DictionaryCache provided mainly to methods, DictionaryCache.byId and DictionaryCache.byValue
     *  usage:
     *     el-expression ${byId('dictname@1.'+item.some_id).value}
     *     where dictname is a predefined dictionary using DictionaryDefine
     * @param context additional environment variables
     * @param col  current column
     * @param var  var name, e.g. row item
     * @param template template for current column
     * @return a instance of cellEditor associated with current column
     */
    protected CellEditor getCellEditor(Map<String, ?> context, Column col, String var, String template) {
        Class[] cacheClass =  {DictionaryCache.class};
        return new ElExpressionExCellEditor(var, template, context, cacheClass);
    }
}
