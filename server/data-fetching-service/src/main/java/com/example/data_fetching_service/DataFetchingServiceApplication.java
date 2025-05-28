package com.example.data_fetching_service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.File;

@SpringBootApplication
public class DataFetchingServiceApplication {

    public static void main(String[] args) {
        // Load environment variables from .env file if it exists
        File dotenvFile = new File(".env");
        if (dotenvFile.exists()) {
            Dotenv dotenv = Dotenv.configure().load();
            dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));
        }

        SpringApplication.run(DataFetchingServiceApplication.class, args);
    }

}
