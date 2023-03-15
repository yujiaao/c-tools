package com.bixuebihui.jmesa.menu;

import org.jmesa.view.html.AbstractHtmlView;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.HtmlSnippets;

/**
 *
 * @author xwx
 * @date 14-3-20
 */
public class MenuHtmlView extends AbstractHtmlView {
    @Override
    public Object render() {
        HtmlSnippets snippets = getHtmlSnippets();

        HtmlBuilder html = new HtmlBuilder();

        html.append(snippets.themeStart());

        html.append(snippets.tableStart());

        html.append(snippets.theadStart());

        html.append(snippets.theadEnd());

        html.append(snippets.tbodyStart());

        html.append(snippets.body());

        html.append(snippets.tbodyEnd());

        html.append(snippets.footer());

        html.append(snippets.toolbar());

        html.append(snippets.tableEnd());

        html.append(snippets.themeEnd());

        html.append(snippets.hiddenFields());

        return html.toString();
    }
}
