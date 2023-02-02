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

import org.jmesa.view.editor.AbstractHeaderEditor;
import org.jmesa.view.html.HtmlBuilder;

/**
 * An implementation of the header editor that displays a checkbox. The checkbox is not actually
 * linked to anything. This is just an example of what a custom header editor would start to look
 * like.
 *
 * @author Jeff Johnston
 */
public class CheckboxHeaderEditor extends AbstractHeaderEditor {

    @Override
    public Object getValue() {
        HtmlBuilder html = new HtmlBuilder();

        html.input().type("checkbox").end();

        return html.toString();
    }

}
