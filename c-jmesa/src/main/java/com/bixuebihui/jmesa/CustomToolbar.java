/*
 * Copyright 2004 original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bixuebihui.jmesa;

import org.jmesa.core.CoreContext;
import org.jmesa.view.html.HtmlUtils;
import org.jmesa.view.html.toolbar.AbstractImageToolbarItem;
import org.jmesa.view.html.toolbar.SeparatorToolbarItem;
import org.jmesa.view.html.toolbar.SimpleToolbar;
import org.jmesa.view.html.toolbar.ToolbarItem;
import org.jmesa.web.WebContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jeff Johnston
 */
public class CustomToolbar extends SimpleToolbar {
    public static final String DELETE = "delete";
    public static final String COPY = "copy";
    public static final String INSERT = "insert";
    public static final String EDIT = "edit";
    public static final String RUN = "run";

    private List<String> buttons;


    @Override
    protected List<ToolbarItem> getToolbarItems() {
        List<ToolbarItem> items = super.getToolbarItems();

        if (enableSeparators) {
            items.add(new SeparatorToolbarItem());
        }
        for (String actionCode : buttons) {
            items.add(createButton(actionCode));
        }
        return items;
    }

    /**
     * to use this function, you must add to table_zh_CN.properties strings
     * html.toolbar.tooltip.ACTIONCODE
     * html.toolbar.text.ACTIONCODE
     * and add a button image with name as image/table/ACTIONCODE.gif
     *
     * @param actionCode
     * @return
     */
    protected ImageToolbarItem createButton(final String actionCode) {
        ImageToolbarItem item = new ImageToolbarItem(this.getCoreContext());

        item.setCode(actionCode);
        item.setTooltip(this.getCoreContext().getMessage("html.toolbar.tooltip." + actionCode));

        item.setImage(getImage(actionCode + ".gif", getWebContext(), getCoreContext()));
        item.setAlt(this.getCoreContext().getMessage("html.toolbar.text." + actionCode));

        return item;
    }

    public void addButton(String actionCode) {
        if (buttons == null) {
            buttons = new ArrayList<String>();
            buttons.add(EDIT);
            buttons.add(INSERT);
            buttons.add(COPY);
            buttons.add(DELETE);
        }
        buttons.add(actionCode);
    }


    private String getImage(String image, WebContext webContext, CoreContext coreContext) {
        String imagesPath = HtmlUtils.imagesPath(webContext, coreContext);
        return imagesPath + image;
    }

    public class ImageToolbarItem extends AbstractImageToolbarItem {

        public ImageToolbarItem(CoreContext coreContext) {
            super(coreContext);
        }

        @Override
        public String render() {
            StringBuilder action = new StringBuilder("javascript:" + getOnInvokeActionJavaScript());
            return enabled(action.toString());
        }

    }

    ;

}
