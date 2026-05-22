package com.biblioteca.multas.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.biblioteca.multas")
@EnableJpaRepositories(basePackages = "com.biblioteca.multas")
@EntityScan(basePackages = "com.biblioteca.multas")
@EnableDiscoveryClient
public class MultasApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultasApplication.class, args);
    }
}
