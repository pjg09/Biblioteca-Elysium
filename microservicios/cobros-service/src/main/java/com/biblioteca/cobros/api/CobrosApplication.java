package com.biblioteca.cobros.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.biblioteca.cobros")
@EnableJpaRepositories(basePackages = "com.biblioteca.cobros")
@EntityScan(basePackages = "com.biblioteca.cobros")
@EnableDiscoveryClient
public class CobrosApplication {

    public static void main(String[] args) {
        SpringApplication.run(CobrosApplication.class, args);
    }
}
