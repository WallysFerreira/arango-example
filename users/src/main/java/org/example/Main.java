package org.example;

import org.example.infrastructure.persistence.ArangoRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Main.class, args);

        ArangoRepository arangoRepository = context.getBean(ArangoRepository.class);
        arangoRepository.testConnection();
    }
}