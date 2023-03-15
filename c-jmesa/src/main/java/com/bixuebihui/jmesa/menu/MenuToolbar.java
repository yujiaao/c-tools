package com.bixuebihui.jmesa.menu;

import org.jmesa.view.html.toolbar.SimpleToolbar;

/**
 *
 * @author super
 * @date 14-3-20
 */
public class MenuToolbar extends SimpleToolbar {

    @Override
    public String render() {

        return "<table border=\"0\"  cellpadding=\"0\"  cellspacing=\"1\" >\n" +
                "\t\t\t<tr>\n" +
                "\t\t\t\t<td><button type='button' onclick=\"addMenuRow('edit_menu')\">新增</button></td>\n" +
                "\t\t\t\t<td><button onclick=\"jQuery.jmesa.setSaveToWorksheet('edit_menu');onInvokeAction('edit_menu','save_worksheet')\">保存</button></td>\n" +
                "\t\t\t\t<td><a href=\"javascript:if (confirm('确定放弃所有更改!')) {jQuery.jmesa.setClearToWorksheet('edit_menu');onInvokeAction('edit_menu','clear_worksheet'),closeWindow()}\">取消</a></td>\n" +
                "\t\t\t\t<td><a href=\"javascript:closeWindow()\">关闭</a></td>\n" +
                "\t\t\t\t<td></td>\n" +
                "\t\t\t</tr>\n" +
                "\t\t</table>";
    }


}
