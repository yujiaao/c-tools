package com.bixuebihui.db;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ActiveRecordTest {

    @Test
    void testReg() {
        String regex = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}(\\.\\d+)?$|^\\d{11,20}$";
        String replacement = "=\"$0\"";
        String s = "2021-01-01 00:00:00.0";
        String s2 = "20110101191101012233";
        String s3="xwx";
        String res = s.replaceAll(regex, replacement);
        assertEquals("=\"2021-01-01 00:00:00.0\"", res);
        res = s2.replaceAll(regex, replacement);
        assertEquals("=\"20110101191101012233\"", res);

        res = s3.replaceAll(regex, replacement);
        assertEquals("xwx", res);
    }
}


