package com.bixuebihui.jmesa;

import com.bixuebihui.jmesa.entry.Query;
import org.jmesa.worksheet.WorksheetColumn;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AbstractWebUITest {

    @Test
    public void testConvert() {
        AbstractWebUI<?, ?> ui = new AbstractWebUI<>() {

            @Override
            protected String getUniquePropertyName() {
                return null;
            }

            @Override
            protected String render(HttpServletRequest request,
                                    HttpServletResponse response) {
                return null;
            }

            @Override
            protected void validateColumn(WorksheetColumn worksheetColumn,
                                          String changedValue) {

            }

            @Override
            protected String render(Query query) {
                return null;
            }

            @Override
            protected String[] getColNames() {
                return null;
            }

            @Override
            protected Object[] getKeys(HttpServletRequest request) {
                return null;
            }
        };

        Class clazz = java.sql.Timestamp.class;
        String value = "2010-11-30 00:00:00.0";
        Object v = ui.convert(value, clazz);
        System.out.println(v);

        clazz = java.sql.Timestamp.class;
        value = "2010-11-30 12:00:00";
        v = ui.convert(value, clazz);
        System.out.println(v);

        clazz = java.sql.Date.class;
        value = "2010-11-30";
        v = ui.convert(value, clazz);
        System.out.println(v);
    }

}
