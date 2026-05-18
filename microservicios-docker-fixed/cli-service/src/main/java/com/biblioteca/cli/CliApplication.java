package com.biblioteca.cli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class CliApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(CliApplication.class, args);
        
        // Obtener el servicio de menú y ejecutarlo
        MenuService menuService = context.getBean(MenuService.class);
        menuService.iniciar();
        
        // Cerrar la aplicación cuando el menú termina
        context.close();
    }
}
