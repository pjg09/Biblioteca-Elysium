package com.biblioteca.cli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
public class CliApplication implements CommandLineRunner {

    @Autowired
    private MenuCli menuCli;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CliApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }

    @Override
    public void run(String... args) {
        menuCli.iniciar();
    }
}
