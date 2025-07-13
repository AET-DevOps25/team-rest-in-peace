package com.rip.notification_service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class NotificationServiceApplication {

    public static void main(String[] args) {
        File dotenvFile = new File(".env");
        if (dotenvFile.exists()) {
            Dotenv dotenv = Dotenv.configure().load();
            dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));
        }

        SpringApplication.run(NotificationServiceApplication.class, args);
    }

}
