package com.bixuebihui.jsongen;

import junit.framework.TestCase;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;

public class GenEsTest extends TestCase {

    public void testGenerate() throws IOException {
        //String sourceDirectory="/Users/xwx/projects/c-tools/c-jsongen/src/test/resources/schema.json";
        String sourceDirectory="https://httpbin.org/json";
        String targetDirectory="/Users/xwx/projects/c-tools/c-jsongen/src/test/java";
        GenEs gen = new GenEs(false, sourceDirectory, targetDirectory,  "com.bixuebihui.hit");
        gen.generate(null, Collections.emptySet(), Collections.emptySet());
    }
    public void testGenerateEsMapping() throws IOException {
        //String sourceDirectory="/Users/xwx/projects/c-tools/c-jsongen/src/test/resources/schema.json";
        String sourceDirectory="http://yz-test1.es6.autohome.com.cn/all-content-info/_mapping";
        String targetDirectory="/Users/xwx/projects/c-tools/c-jsongen/src/test/java";
        boolean isSchema = true;
        GenEs gen = new GenEs(isSchema, sourceDirectory, targetDirectory, "com.bixuebihui.hit");
        gen.generate(stringPair -> System.out.println(stringPair.getLeft()+":"+ Arrays.toString(stringPair.getRight())), Set.of("duaration"), Set.of("play_url"));
    }
}
