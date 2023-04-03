package com.bixuebihui.jmesa;

import org.jmesa.model.PageItems;
import org.jmesa.model.TableModel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author xwx
 */
public class SimpleTable extends BasicWebUI {

    private PageItems items;

    public SimpleTable(PageItems items) {
        this.items = items;
    }

    @Override
    protected void setDataAndLimitVariables(TableModel tableModel) {
        tableModel.setItems(items);
    }

    public void exportExcel(HttpServletRequest request,
                            HttpServletResponse response) {
        TableModel tableModel = new TableModel(id, request, response);

        this.exportRender(tableModel);

    }
}
