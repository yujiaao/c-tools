package com.bixuebihui.jmesa;

import com.bixuebihui.jdbc.IBaseListService;
import com.bixuebihui.util.ParameterUtils;
import org.jmesa.core.preference.Preferences;
import org.jmesa.limit.Limit;
import org.jmesa.limit.LimitFactory;
import org.jmesa.model.ExportTypes;
import org.jmesa.model.TableModel;
import org.jmesa.model.TableModelUtils;
import org.jmesa.view.component.Column;
import org.jmesa.view.component.Row;
import org.jmesa.view.component.Table;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author xwx
 */
public class BasicWebUI extends AbstractWebUI<Object, Long> {

    /**
     * @see org.jmesa.limit.LimitActionFactoryJsonImpl json query, use LimitActionFactoryJsonImpl
     */
    public static String JSON_QUERY = "JSON_QUERY";
    protected int maxRows = 0;
    protected int[] maxRowsIncrements = new int[]{maxRows};
    private String uniquePropertyName;
    /**
     * @see SimpleView  Only simple table, not toolbar and etc
     */
    private boolean useSimpleView = false;
    /**
     * 是否使用日期区间控件，如使用则每个控件增加两个参数：起始和截止日期，与coreSql里的参数相对应
     * 参数需按顺序放在最后
     * // every dateRange increase the params by 2
     */
    private int useDateRange = 0;
    /**
     * comma separated string
     */
    private String colsList;

    @Override
    protected String getUniquePropertyName() {
        return uniquePropertyName;
    }

    public void setUniquePropertyName(String uniquePropertyName) {
        this.uniquePropertyName = uniquePropertyName;
    }

    protected TableModel createTableModel(String id, HttpServletRequest request,
                     HttpServletResponse response ){

        TableModel model = new TableModel(id, request, response);

        // use Database filter & sort, so don't need java custom filter match
        model.autoFilterAndSort(false);
        return model;
    }

    @Override
    protected String render(HttpServletRequest request,
                            HttpServletResponse response) {

        TableModel tableFacade = createTableModel(id, request, response);

        Object json = request.getAttribute(JSON_QUERY);
        if (json != null) {
            LimitFactory limitFactory = (json instanceof Map) ? new LimitFactory(id, (Map<String, Object>) json) :
                    new LimitFactory(id, json.toString());
            Limit limit = limitFactory.createLimit();
            tableFacade.setLimit(limit);
        }

        tableFacade.setExportTypes(ExportTypes.CSV, ExportTypes.EXCEL);

        Preferences preferences = null;
        tableFacade.setPreferences(preferences);

        if (maxRows > 0) {
            tableFacade.setMaxRows(maxRows);
            tableFacade.setMaxRowsIncrements(maxRowsIncrements);
        }

        // exports to use.
        String[] colNames = getColNames();
        Table table = tableFacade.isExporting() ? TableModelUtils.createTable(colNames) : TableModelUtils.createHtmlTable(colNames);

        tableFacade.setTable(table);

        setDataAndLimitVariables(tableFacade);


        if (isUseSimpleView()) {
            tableFacade.setView(new SimpleView());
        }

        this.setTitles(table, id, getColNames(), true);
        final Row row = table.getRow();
        row.setUniqueProperty(getUniquePropertyName());

        List<Column> cols = row.getColumns();

        Map<String, Object> context = getContext(request);

        for (Column col : cols) {
            prepareCellEditor(col, context);
        }


        if (tableFacade.isExporting()) {
            // Will write the export data out to the response.
            exportRender(tableFacade);
            // In Spring return null tells the controller not to do anything.
            return null;
        } else {
            return tableFacade.render();
        }
    }

    public String getColsList() {
        return colsList;
    }

    public void setColsList(String colsList) {
        this.colsList = colsList;
    }

    public IBaseListService<?, ?> getListService() {
        return this.service;
    }

    @Override
    protected String[] getColNames() {
        return getColsList().split("\\,");
    }

    @Override
    protected Long[] getKeys(HttpServletRequest request) {
        return ParameterUtils.getArrayLong(request, checkboxName);
    }

    public boolean isUseSimpleView() {
        return useSimpleView;
    }

    public void setUseSimpleView(boolean useSimpleView) {
        this.useSimpleView = useSimpleView;
    }

    public int getUseDateRange() {
        return useDateRange;
    }

    public void setUseDateRange(int useDateRange) {
        this.useDateRange = useDateRange;
    }


}
