package com.bixuebihui.generated.tablegen.web;
/**
 * T_metatable
 * <p>
 * WARNING! Automatically generated file!
 * Do not edit the pojo and dal packages,use bixuebihui-smartable!
 * Code Generator by J.A.Carter
 * Modified by Xing Wanxiang 2008
 * (c) www.goldjetty.com
 */

import com.bixuebihui.generated.tablegen.business.T_metatableManager;
import com.bixuebihui.generated.tablegen.pojo.T_metatable;
import com.bixuebihui.jmesa.AbstractWebUI;
import com.bixuebihui.jmesa.entry.Query;
import org.jmesa.worksheet.WorksheetColumn;

import jakarta.servlet.http.HttpServletRequest;

public class T_metatableWebUI extends AbstractWebUI<T_metatable, Long> {


    @Override
    protected String getUniquePropertyName() {
        return "tid";
    }

    ;

    public void setService(T_metatableManager service) {
        this.service = service;
    }

    /**
     * An example of how to validate the worksheet column cells.
     */
    @Override
    protected void validateColumn(WorksheetColumn worksheetColumn, String changedValue) {
        if ("foo".equals(changedValue)) {
            worksheetColumn.setErrorKey("foo.error");
        } else {
            worksheetColumn.removeError();
        }
    }

    @Override
    protected String render(Query query) {
        return null;
    }

    @Override
    protected String[] getColNames() {
        String[] colNames = {"chkbox", "tid", "tname", "isnode", "isstat", "isversion", "isuuid", "ismodifydate", "extrainterfaces", "extrasuperclasses", "description"};
        return colNames;
    }

    @Override
    protected Long[] getKeys(HttpServletRequest request) {
        String[] res = request.getParameterValues(checkboxName);
        if (res == null) {
            return new Long[0];
        }

        return (Long[]) converter.convert(
                request.getParameterValues(checkboxName), Long.class);
    }


}
