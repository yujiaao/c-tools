package com.bixuebihui.jmesa;

import org.jmesa.view.html.AbstractHtmlView;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.HtmlSnippets;

/**
 * @author Jeff Johnston
 */
public class CustomView extends AbstractHtmlView {

    @Override
    public Object render() {
        HtmlSnippets snippets = getHtmlSnippets();

        HtmlBuilder html = new HtmlBuilder();

        html.append(snippets.themeStart());

        html.append(snippets.tableStart());

        html.append(snippets.theadStart());

        html.append(snippets.toolbar());

        html.append(snippets.filter());

        html.append(snippets.header());

        html.append(snippets.theadEnd());

        html.append(snippets.tbodyStart());

        html.append(snippets.body());

        html.append(snippets.tbodyEnd());

        html.append(snippets.footer());

        html.append(snippets.statusBar());

        html.append(snippets.tableEnd());

        html.append(snippets.themeEnd());

        getWebContext().setRequestAttribute("jmesaonload", snippets.hiddenFields());

        return html.toString();
    }
}
