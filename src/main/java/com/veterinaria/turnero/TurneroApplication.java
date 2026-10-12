package com.veterinaria.turnero;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.veterinaria.turnero")
public class TurneroApplication {
    public static void main(String[] args) {
        SpringApplication.run(TurneroApplication.class, args);
    }
}
