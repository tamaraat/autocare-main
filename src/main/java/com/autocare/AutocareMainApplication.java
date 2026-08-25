package com.autocare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AutocareMainApplication {

    public static void main(String[] args) {

        SpringApplication.run(
                AutocareMainApplication.class,
                args
        );
    }
}