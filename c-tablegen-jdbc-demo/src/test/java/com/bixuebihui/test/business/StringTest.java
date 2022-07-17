package com.bixuebihui.test.business;

import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringTest {


    @Test
    void test(){
        Assertions.assertEquals("?,?", StringUtils.join(StringUtils.repeat("?",
                2).split(""),","));
    }
}
