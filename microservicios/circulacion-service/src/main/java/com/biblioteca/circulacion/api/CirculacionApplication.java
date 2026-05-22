package com.biblioteca.circulacion.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.biblioteca.circulacion")
@EnableJpaRepositories(basePackages = "com.biblioteca.circulacion")
@EntityScan(basePackages = "com.biblioteca.circulacion")
@EnableFeignClients(basePackages = "com.biblioteca.circulacion")
public class CirculacionApplication {

    public static void main(String[] args) {
        SpringApplication.run(CirculacionApplication.class, args);
    }
}
