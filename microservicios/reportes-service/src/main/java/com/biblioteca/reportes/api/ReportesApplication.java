package com.biblioteca.reportes.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.biblioteca.reportes")
@EnableJpaRepositories(basePackages = "com.biblioteca.reportes")
@EntityScan(basePackages = "com.biblioteca.reportes")
public class ReportesApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReportesApplication.class, args);
    }
}
