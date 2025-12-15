package com.bixuebihui.util.html;

/**
 * 形成HTML表单控件
 *
 * @author xingwx
 */
public class FormControl {

    public static String makeCheckBox(String idName, String name, String value,
                                      String title, boolean checked, boolean disable)
    {

        return "<label for=" + idName + "><input title=" + title + " type=checkbox" +
                " name=" + name
                + " id=" + idName
                + " value=\"" + (value == null ? "" : value)
                + "\" width=\"120\" size=\"10\" " + (checked ? " checked='checked' " : " ")
                + (disable ? " disable='disable' " : " ") + " >" + title + "</label>";

    }

    public static String makeInputBox(String idName, String name, String Value, String title)//"chk")
    {

        return "<input title=\"" + title + "\" type=text" +
                " name=\"" + name
                + "\" id=\"" + idName
                + "\" value=\"" + (Value == null ? "" : Value)
                + "\"  width=\"120\"  size=\"10\" >";

    }

    /**
     * 日期选择控件，需在JSP页面里引入/js/button_control_time.js 如
     * &lt;script type="text/javascript" src="../js/button_control_time.js" &gt;&lt;/script&gt;
     *
     * @param idName
     * @param controlName
     * @param value
     * @return HTML代码
     */
    public static String dateControl(String idName, String controlName, String value, String title) {
        return "<input type=\"text\" size=\"7\" name=\"" + controlName
                + "\" id=\"" + idName + "\" value=\"" + ((value == null || "null".equals(value)) ? "" : value) + "\" title=\"" + title + "\">\r\n"
                + "       <IMG id=\"imgDate1\" onmouseover=\"RaiseButton(this)\" style=\"cursor: pointer\" "
                + " onclick=\"javascript:selectDate(document.getElementById('" + idName + "'), event)\"\r\n"
                + "\t\t\t\tonmouseout=\"HideButton(this)\" height=\"20\" src=\"../images/CALENDAR.GIF\" width=\"16\"\r\n"
                + "\t\t\t\talign=\"absMiddle\" border=\"0\" >\r\n";
    }

    /**
     * 日期选择控件，需在JSP页面里引入
     * &gt;link href="../../js/datepicker/whyGreen/datepicker.css" rel="stylesheet" type="text/css"&lt;
     * &gt;script type="text/javascript" src="../../js/datepicker/WdatePicker.js"&lt;&gt;/script&lt;
     *
     * @param idName
     * @param controlName
     * @param value
     * @return 新样式日期控件的HTML代码
     */
    public static String dateControlAlt(String idName, String controlName, String value, String title) {
        return "<input type=\"text\" size=\"18\" name=\"" + controlName
                + "\" id=\"" + idName + "\" value=\"" + ((value == null || "null".equals(value)) ? "" : value)
                + "\" title=\"" + title
                + "\"   class=\"Wdate\" onFocus=\"WdatePicker({isShowClear:false})\">\r\n";
    }


    /**
     * 形成单选列表框
     *
     * @param nameArray
     * @param valueArray
     * @param strDefaultSelect
     * @param strConditionSelect
     * @return HTML代码，option,不包括select
     */
    public static String getOptionList(String[] nameArray,
                                       int[] valueArray,
                                       String strDefaultSelect, String strConditionSelect) {
        String strList = "";
        boolean ifDefault = false;
        boolean ifCondition = false;
        if (strConditionSelect != null && !"".equals(strConditionSelect)) {
            ifCondition = true;
        } else if (strDefaultSelect != null && !"".equals(strDefaultSelect)) {
            ifDefault = true;
        }

        if (valueArray.length == 0) {
            return "<option value='0'>(未知)</option>";
        }
        for (int loop = 0; loop < valueArray.length; loop++) {

            String isSelected = "";

            if (ifCondition) {
                if ((valueArray[loop] + "").equals(strConditionSelect)) {
                    isSelected = "selected";
                }
            } else if (ifDefault) {
                if ((valueArray[loop] + "").equals(strDefaultSelect)) {
                    isSelected = "selected";
                }
            }
            strList += "<option value='" + valueArray[loop] + "' " + isSelected + " title=\"" + nameArray[loop] + "\">" + nameArray[loop] + "</option>";
        }
        return strList;
    }


    public static String getOptionList(String[] nameArray,
                                       String[] valueArray,
                                       String strDefaultSelect, String strConditionSelect) {
        String strList = "";
        boolean ifDefault = false;
        boolean ifCondition = false;
        if (strConditionSelect != null && !"".equals(strConditionSelect)) {
            ifCondition = true;
        } else if (strDefaultSelect != null && !"".equals(strDefaultSelect)) {
            ifDefault = true;
        }

        if (valueArray.length == 0) {
            return "<option value='0'>(未知)</option>";
        }
        for (int loop = 0; loop < valueArray.length; loop++) {

            String isSelected = "";

            if (ifCondition) {
                if ((valueArray[loop] + "").equals(strConditionSelect)) {
                    isSelected = "selected";
                }
            } else if (ifDefault) {
                if ((valueArray[loop] + "").equals(strDefaultSelect)) {
                    isSelected = "selected";
                }
            }
            strList += "<option value='" + valueArray[loop] + "' " + isSelected + "  title=\"" + nameArray[loop] + "\">" + nameArray[loop] + "</option>";
        }
        return strList;
    }

    /**
     * 形成多选列表
     *
     * @param nameArray
     * @param valueArray
     * @param strDefaultSelect
     * @param strConditionSelect
     * @return HTML代码, input check box
     */
    public static String getCheckList(String name, String[] nameArray,
                                      int[] valueArray,
                                      String strDefaultSelect, String strConditionSelect, String separator) {
        String strList = "";
        boolean ifDefault = false;
        boolean ifCondition = false;
        if (strConditionSelect != null && !"".equals(strConditionSelect)) {
            ifCondition = true;
        } else if (strDefaultSelect != null && !"".equals(strDefaultSelect)) {
            ifDefault = true;
        }


        for (int loop = 0; loop < valueArray.length; loop++) {

            String isSelected = "";

            if (ifCondition) {
                if ((valueArray[loop] + "").equals(strConditionSelect)) {
                    isSelected = "checked";
                }
            } else if (ifDefault) {
                if ((valueArray[loop] + "").equals(strDefaultSelect)) {
                    isSelected = "checked";
                }
            }
            strList += "<input type=checkbox  name='" + name + "' id='" + name + loop
                    + "' value='" + valueArray[loop] + "' " + isSelected + "><label for='" + name + loop + "'>" + nameArray[loop] + "</label>" + separator;
        }

        return strList;
    }
}
