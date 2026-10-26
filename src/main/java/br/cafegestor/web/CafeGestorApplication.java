package br.cafegestor.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da aplicação web. Sobe o Tomcat embutido, publica a API REST em /api
 * e serve as páginas do front-end (pasta resources/static).
 */
@SpringBootApplication
public class CafeGestorApplication {
    public static void main(String[] args) {
        SpringApplication.run(CafeGestorApplication.class, args);
    }
}
