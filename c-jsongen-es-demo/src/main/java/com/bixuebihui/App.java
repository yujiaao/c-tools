package com.bixuebihui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author xwx
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.bixuebihui.**")
public class App {



    public static void main(String[] args) {
        SpringApplication.run(App.class, args);

    }


}
