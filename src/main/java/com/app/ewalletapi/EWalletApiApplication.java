package com.app.ewalletapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EWalletApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(EWalletApiApplication.class, args);
    }

}
