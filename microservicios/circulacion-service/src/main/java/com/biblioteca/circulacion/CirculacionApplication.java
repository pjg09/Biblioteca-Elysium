package com.biblioteca.circulacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CirculacionApplication {

    public static void main(String[] args) {
        SpringApplication.run(CirculacionApplication.class, args);
    }
}
