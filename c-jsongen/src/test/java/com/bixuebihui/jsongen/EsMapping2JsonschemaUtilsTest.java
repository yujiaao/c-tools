package com.bixuebihui.jsongen;

import com.fasterxml.jackson.databind.JsonNode;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class EsMapping2JsonschemaUtilsTest extends TestCase {

    public static  String s;

    static {
        try {
            s = IOUtils.resourceToString("/test.json", StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testTrans() throws IOException {

        JsonNode trans = EsMapping2JsonschemaUtils.trans(s, Collections.emptySet(), Collections.emptySet());
        System.out.println(trans.toString());
        //  https://www.jsonschema2pojo.org/
    }
}
