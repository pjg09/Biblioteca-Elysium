package com.biblioteca.materiales.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.biblioteca.materiales")
@EnableJpaRepositories(basePackages = "com.biblioteca.materiales")
@EntityScan(basePackages = "com.biblioteca.materiales")
public class MaterialesApplication {

    public static void main(String[] args) {
        SpringApplication.run(MaterialesApplication.class, args);
    }
}
