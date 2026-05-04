package com.biblioteca.cobros;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CobrosApplication {

    public static void main(String[] args) {
        SpringApplication.run(CobrosApplication.class, args);
    }
}
