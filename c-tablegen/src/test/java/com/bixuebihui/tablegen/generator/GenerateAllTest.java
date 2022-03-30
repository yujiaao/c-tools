package com.bixuebihui.tablegen.generator;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class GenerateAllTest {

    @Test
    void run() {
        GenerateAll gen = new GenerateAll();

        gen.run(Objects.requireNonNull(this.getClass().getResource("/tablegen.properties")).getPath());
    }

    @Test
    void runJson() {
        GenerateAll gen = new GenerateAll();

        gen.run(Objects.requireNonNull(this.getClass().getResource("/es.properties")).getPath());
    }
}
