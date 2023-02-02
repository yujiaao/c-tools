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

import org.jmesa.limit.Limit;
import org.jmesa.limit.Order;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlColumn;
import org.jmesa.view.html.editor.HtmlHeaderEditor;

/**
 * @author Jeff Johnston
 * @since 2.2
 */
public class CustomHeaderEditor extends HtmlHeaderEditor {

    /**
     * @param currentOrder The current sort Order.
     * @param column       The current column.
     * @param limit        The current limit.
     * @return The JavaScript to get the next Order when invoking the onlick command.
     */
    @Override
    protected String onclick(Order currentOrder, HtmlColumn column, Limit limit) {
        HtmlBuilder html = new HtmlBuilder();

        if (currentOrder == Order.NONE) {
            html.onclick("removeSortFromLimit('" + limit.getId() + "','" + column.getProperty() + "');onInvokeAction('" + limit.getId() + "')");
        } else {
            html.onclick("addOrderedSortToLimit('" + limit.getId() + "','" + column.getProperty() + "','" + currentOrder.toParam() + "');"
                    + getOnInvokeActionJavaScript(limit));
        }

        return html.toString();
    }
}
