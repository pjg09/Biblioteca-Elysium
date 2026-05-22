package com.biblioteca.prestamosexternos.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.biblioteca.prestamosexternos")
@EnableJpaRepositories(basePackages = "com.biblioteca.prestamosexternos")
@EntityScan(basePackages = "com.biblioteca.prestamosexternos")
public class PrestamosExternosApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrestamosExternosApplication.class, args);
    }
}
