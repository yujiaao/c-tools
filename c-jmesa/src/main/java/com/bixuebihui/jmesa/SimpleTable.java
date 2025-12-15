package com.bixuebihui.jmesa;

import org.jmesa.model.PageItems;
import org.jmesa.model.TableModel;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
        TableModel tableModel = new TableModel(this.id, request, response);

        this.exportRender(tableModel);

    }
}
