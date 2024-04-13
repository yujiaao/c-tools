package com.bixuebihui.excel;

import org.apache.poi.ss.usermodel.*;

/**
 * @author xwx
 */
public class BaseExcelOp {

    protected Workbook workbook = null;
    protected Sheet curSheet = null;

    public BaseExcelOp() {
        super();
    }

    public Sheet getNewSheet(String sheetName) {
        curSheet = workbook.createSheet(sheetName);
        return curSheet;
    }

    public Row getNewRow(int row) {
        return curSheet.createRow((short) row);
    }

    public Cell createStringCell(Row row, int col, String value) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        return cell;
    }

    public Cell createNumericalCell(Row row, int col, int value) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        return cell;
    }

    public Cell createNumericalCell(Row row, int col, double value) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        return cell;
    }

}
