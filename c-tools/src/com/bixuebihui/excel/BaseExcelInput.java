package com.bixuebihui.excel;

import com.bixuebihui.util.DateConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author xwx
 */
public class BaseExcelInput {

    private static final  Logger LOG = LoggerFactory.getLogger(BaseExcelInput.class);


    protected static
    String getCellContent(Row row, int i) {

        Cell cell = row.getCell(i);

        if (cell == null) {
            return "";
        }
        CellType celltype = cell.getCellType();

        if (celltype == CellType.STRING) {
            return cell.getRichStringCellValue().getString().trim();
        } else if (celltype == CellType.NUMERIC) {
            double v = cell.getNumericCellValue();
            if (v > 1e6) {
                //可能为手机号或身份证号等，避免生成科学记数法的格式，如1.3910010327e10
                return new DecimalFormat("#").format(v);
            }
            return "" + v;
        } else if (celltype == CellType.BLANK) {
            return "";
        } else if(celltype == CellType.FORMULA) {
            return cell.getStringCellValue();
        }else{
            return "" + cell.getNumericCellValue();
        }
    }

    protected static int getIntCellContent(Row row, int i) {
        return (int) getDoubleCellContent(row, i);
    }

    protected static long getLongCellContent(Row row, int i) {
        return (long) getDoubleCellContent(row, i);
    }

    protected static double getDoubleCellContent(Row row, int i) {

        Cell cell = row.getCell(i);
        if (cell == null) {
            return 0;
        }
        CellType cellType = cell.getCellType();

        if (cellType == CellType.STRING) {
            String tmp = cell.getRichStringCellValue().getString().trim();
            if (StringUtils.isEmpty(tmp)) {
                return 0;
            }
            return Double.parseDouble(tmp);
        } else {
            return cell.getNumericCellValue();
        }
    }

    /**
     * 时间;
     */
    private static java.util.Date getDateCell(Row row, int i) {

        java.util.Date changedCell;

        changedCell = row.getCell(i).getDateCellValue();

        return changedCell;
    }

    /**
     * 时间转换String转换成Date 可自动判断年月日分隔型;‘-’分隔型;数字型；
     */
    protected static java.util.Date getDateCellContent(Row row, int i) {
        CellType cellType = row.getCell(i).getCellType();

        if (cellType == CellType.NUMERIC) {
            return getDateCell(row, i);
        } else {
            String tmp = getCellContent(row, i);
            if ( tmp.contains("-")) {
                return getDateCellStringContentWithSeparator(row, i);
            } else {
                return getDateCellStringContent(row, i);
            }
        }
    }

    /**
     * 时间转换String转换成Date ‘-’分隔型;
     */
    private static java.util.Date getDateCellStringContentWithSeparator(Row row, int i) {

        String changedCell = "";


        CellType celltype = row.getCell(i).getCellType();

        if (celltype == CellType.STRING) {
            changedCell = row.getCell(i).getRichStringCellValue().getString();
        } else if (celltype == CellType.NUMERIC) {
            changedCell = "" + (int) row.getCell(i).getNumericCellValue();
        }

        return parseDate(changedCell);

    }

    /**
     * 使用时间转换String转换成Date 年月日分隔型;
     */
    private static java.util.Date getDateCellStringContent(Row row, int i) {

        String changedCell = "";
        String year = "";
        String month = "";
        String day = "";
        String postTime;


        CellType cellType = row.getCell(i).getCellType();

        if (cellType == CellType.STRING) {
            changedCell = row.getCell(i).getRichStringCellValue().getString();
        } else if (cellType == CellType.NUMERIC) {
            changedCell = "" + (int) row.getCell(i).getNumericCellValue();
        }

        if (changedCell.contains("年")) {
            year = changedCell.substring(0, changedCell.indexOf('年'));
            if (changedCell.contains("月")) {
                month = changedCell.substring(changedCell.indexOf('年') + 1,
                        changedCell.indexOf('月'));

                if (changedCell.contains("日")) {
                    day = changedCell.substring(changedCell.indexOf('月') + 1,
                            changedCell.indexOf('日'));
                }
            }
        }

        if ("".equals(year)) {
            year = "1900";
        }
        if ("".equals(month)) {
            month = "01";
        }
        if ("".equals(day)) {
            day = "01";
        }

        postTime = year + "-" + month + "-" + day;

        return parseDate(postTime);

    }

    private static Date parseDate(String postTime) {
        Date res = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "yyyy-MM-dd");
            java.util.Date date = formatter.parse(postTime);
             res = new Date(date.getTime());
        } catch (ParseException e) {
            LOG.warn("", e);
        }
        return  res;
    }

    @Deprecated
    public static java.util.Date makeDate(String year, String month, String day) {
       return DateConverter.makeDate(year, month, day);
    }


    /**
     * 从 excel文件的一列或两列中获取全部可能的字典表的值
     *
     * @param fileName    excel文件名
     * @param sheetName   excel中的sheet名
     * @param keyRowNum   字典的KEY值
     * @param valueRowNum 字典的VALUE值，如果和 keyRowNum一样，则认为没有key值，key值将采用数据库生成的ID
     *                    忽略Excel的表头第1行
     * @return 包含key->value对的hashtable
     */
    public static Map<String, String> findDictionary(String fileName, String sheetName,
                                                     int keyRowNum, int valueRowNum) {
        return findDictionary(fileName, sheetName, keyRowNum, valueRowNum, 1);
    }

    /**
     * 从 excel文件的一列或两列中获取全部可能的字典表的值
     *
     * @param fileName    excel文件名
     * @param sheetName   excel中的sheet名
     * @param keyRowNum   字典的KEY值
     * @param valueRowNum 字典的VALUE值，如果和 keyRowNum一样，则认为没有key值，key值将采用数据库生成的ID
     * @param skipLines   忽略Excel的表头行数
     * @return 包含key->value对的hashtable
     */
    public static Map<String, String> findDictionary(String fileName, String sheetName,
                                                           int keyRowNum, int valueRowNum, int skipLines) {
        Map<String, String> ht = new HashMap<>(32);

        Workbook workbook;
        Sheet sheet1;
        FileInputStream getExcelContent;

        try {
            // 创建一个POIFSFileSystem来读取Excel文档;

            File excelFile = new File(fileName);

            if (!excelFile.exists()) {
                LOG.warn("检测到文件不存在");
                return ht;
            }
            getExcelContent = new FileInputStream(excelFile);
            // 把文件通过输入流读取出来;
            POIFSFileSystem fileSystem = new POIFSFileSystem(getExcelContent);

            // 从POIFSFileSystem获得一个HSSF workbook;
            if(fileName.endsWith(".xls")) {
                workbook = new HSSFWorkbook(fileSystem);
            }else if( fileName.endsWith(".xlsx")){
                throw new IllegalArgumentException("to support xlsx add jars, and change this line of code");
            }else{
                throw new IllegalArgumentException("unknown file extension");
            }

            // 从Excel workbook获得一个Excel电子表格;
            sheet1 = workbook.getSheet(sheetName);
            Iterator<Row> rowIterator = sheet1.rowIterator();
            Row row;

            for (int i = 0; i < skipLines; i++) {
                rowIterator.next();
                // skip first line!
            }
            while (rowIterator.hasNext()) {
                row = rowIterator.next();

                if (row != null) {
                    String tmp;
                    String tmp1;
                    if (keyRowNum == valueRowNum) {
                        tmp1 = filterOutSpace(getCellContent(row, keyRowNum));
                        tmp = tmp1;
                    } else {
                        tmp = filterOutSpace(getCellContent(row, keyRowNum));
                        tmp1 = filterOutSpace(getCellContent(row, valueRowNum));
                    }
                    if (tmp != null && tmp1 != null && !"0.0".equals(tmp)) {

                        if (!ht.containsKey(tmp.trim())
                                && !"".equals(tmp.trim())) {
                            ht.put(tmp.trim(), tmp1.trim());
                            LOG.info("成功导入" + ht.size() + ": " + tmp
                                    + ", " + tmp1);
                        }
                    }
                }
            }

        } catch (Exception e) {
            LOG.warn("", e);
        }
        return ht;
    }


    public BaseExcelInput() {
        super();
    }

    protected static String filterOutSpace(String src) {
        src = StringUtils.stripToNull(src);
        if (src == null) {
            return null;
        }
        if (src.matches("^[0-9a-zA-Z].*")) {
            //如是英文或数字，略去最后一步
            return src;
        }
        //去掉中文中的空格，主要是两字人名时要用去中间对齐用的空格。
        return src.replaceAll(" ", "").replaceAll("\u00A0", "");
    }
}
