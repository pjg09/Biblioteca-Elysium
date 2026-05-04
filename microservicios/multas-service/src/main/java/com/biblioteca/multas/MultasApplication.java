package com.biblioteca.multas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MultasApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultasApplication.class, args);
    }
}
