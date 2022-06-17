package com.bixuebihui.test.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class MyTestControllerTest {

    @Autowired
    MyTestController controller;

    @Test
    void update() {
        String res = controller.update(3);
        assertEquals("OK", res);
    }
}
