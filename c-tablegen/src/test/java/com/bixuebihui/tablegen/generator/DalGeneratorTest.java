package com.bixuebihui.tablegen.generator;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class DalGeneratorTest {
    @Test
    void getExtendsClasses() throws IOException {
        DalGenerator gen = new DalGenerator();
        gen.init(this.getClass().getResource("/tablegen.properties").getPath());
        String res = gen.generate("test_gen", 0);

        System.out.println(res);
    }

    @Test
    void isNotEmpty() {
    }

    @Test
    void getColType() {
    }

    @Test
    void getFirstKeyType() {
    }

    @Test
    void getFirstKeyName() {
    }

    @Test
    void containsVersion() {
    }

    @Test
    void makeInsertObjects() {
    }

    @Test
    void makeUpdateObjects() {
    }

    @Test
    void createKeyObjects() {
    }

    @Test
    void mapRow() {
    }

    @Test
    void testGetFirstKeyType() {
    }

    @Test
    void testGetFirstKeyName() {
    }
}
