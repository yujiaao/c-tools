package com.bixuebihui.jsongen;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class GenEsTest  {

    @Test
    public void testGenerate() throws IOException {
        //String sourceDirectory="/Users/xwx/projects/c-tools/c-jsongen/src/test/resources/schema.json";
        String sourceDirectory="https://httpbin.org/json";
        String targetDirectory="/Users/xwx/projects/c-tools/c-jsongen/src/test/java";
        GenEs gen = new GenEs(false, sourceDirectory, targetDirectory,  "com.bixuebihui.hit", null, null, null);
        gen.generate(null, Collections.emptySet(), Collections.emptySet(), null);
    }
    @Test
    public void testGenerateEsMapping() throws IOException {
       // String sourceDirectory="src/test/resources/mapping.json";
        /*es6*/ // String sourceDirectory="http://localhost:9200/my-index-001/_mapping.json";
        /*es7*/String sourceDirectory="http://localhost:9200/chejiahao-info-source/_mapping";

        String targetDirectory="/Users/xwx/projects/c-tools/c-jsongen/src/test/java";
        boolean isSchema = true;
        GenEs gen = new GenEs(isSchema, sourceDirectory, targetDirectory, "com.bixuebihui.hit", null, null, null);
        gen.generate(stringPair -> System.out.println(stringPair.getLeft()+":"+
                Arrays.toString(stringPair.getRight())), Set.of("duaration"),
                Set.of("play_url"), null);
    }

    @Test
    public void testGenerateEsMappingWithNestedObject() throws IOException {
        //String sourceDirectory="/Users/xwx/projects/c-tools/c-jsongen/src/test/resources/schema.json";
        String sourceDirectory="/Users/xwx/projects/c-tools/c-jsongen/src/test/resources/schema_nested_object.json";
        String targetDirectory="/Users/xwx/projects/c-tools/c-jsongen/src/test/java";
        boolean isSchema = true;
        GenEs gen = new GenEs(isSchema, sourceDirectory, targetDirectory, "com.bixuebihui.hit", null, null, null);
        gen.generate(stringPair -> System.out.println(stringPair.getLeft()+":"+
                        Arrays.toString(stringPair.getRight())),
                Set.of("duaration"),
                Set.of("play_url"),
                null
        );
    }
}
