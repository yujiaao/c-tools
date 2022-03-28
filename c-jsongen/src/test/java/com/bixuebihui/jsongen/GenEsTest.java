package com.bixuebihui.jsongen;

import junit.framework.TestCase;

import java.io.IOException;

public class GenEsTest extends TestCase {

    public void testGenerate() throws IOException {
        //String sourceDirectory="/Users/xwx/projects/c-tools/c-jsongen/src/test/resources/schema.json";
        String sourceDirectory="https://httpbin.org/json";
        String targetDirectory="/Users/xwx/projects/c-tools/c-jsongen/src/test/java";
        GenEs gen = new GenEs(sourceDirectory, targetDirectory, false);
        gen.generate();
    }
    public void testGenerateEsMapping() throws IOException {
        //String sourceDirectory="/Users/xwx/projects/c-tools/c-jsongen/src/test/resources/schema.json";
        String sourceDirectory="http://yz-test1.es6.autohome.com.cn/mobile-video-info/_mapping";
        String targetDirectory="/Users/xwx/projects/c-tools/c-jsongen/src/test/java";
        boolean isSchema = true;
        GenEs gen = new GenEs(sourceDirectory, targetDirectory, isSchema);
        gen.generate();
    }
}
