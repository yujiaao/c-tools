package com.bixuebihui.tablegen;

import com.google.common.base.CaseFormat;
import org.apache.commons.text.CaseUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NameUtilsTest {

    @Test
    void isYes() {
    }

    @Test
    void columnNameToFieldName() {
    }

    @Test
    void tableNameToClassName() {
    }

    @Test
    void columnNameToConstantName() {
    }

    @Test
    void firstUp() {
        String res = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, "MyNameIs"); // return MyNameIs
        assertEquals(res, "MyNameIs");

        res = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, "myNameIs"); // return MyNameIs
        assertEquals(res, "MyNameIs");

        res =  "my_NameIs_abc".replaceAll("([A-Z])","_$1");


        res = CaseUtils.toCamelCase(res, true, '_', '-',' '); // return MyNameIs

        assertEquals("MyNameIsAbc",res);

    }

    @Test
    void firstLow() {
    }

    @Test
    void getConfigBaseDir() {
    }
}
