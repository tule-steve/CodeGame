package com.codegame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CodeGameApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(CodeGameApplication.class);
        springApplication.run(args);
    }
}
