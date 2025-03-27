package com.bixuebihui.test.converter;

import com.bixuebihui.test.DataAutoConfiguration;
import com.bixuebihui.test.dto.TestGenDto;
import com.bixuebihui.test.pojo.TestGen;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes = {
            TestGenConverter.class
        }
)
@EnableAutoConfiguration
@ContextConfiguration(
        classes = {
            DataAutoConfiguration.class
        }
)
@ComponentScan(
        basePackages = {
            "com.bixuebihui.test.**"
        }
)
class TestGenConverterTest {
    @Autowired
    private TestGenConverter testGenConverter;


    @Test
    void toDto() {
        TestGen info = new TestGen();
        info.setName("name");

        TestGenDto res = testGenConverter.toDto(info);
    }

    @Test
    void toDao() {
    }

    @Test
    void toDtoList() {
    }

    @Test
    void toDaoList() {
    }
}
