package com.bixuebihui.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.List;


/**
 * @author xwx
 */
public abstract class BaseExcelOutput extends BaseExcelOp {

    public BaseExcelOutput() {
        workbook = new HSSFWorkbook();
    }

    abstract String[] getColNames();

    abstract void export(List vector, String[] colNames);



}
