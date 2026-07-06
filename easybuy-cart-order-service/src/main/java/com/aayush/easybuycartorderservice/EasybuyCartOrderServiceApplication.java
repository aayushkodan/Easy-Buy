package com.aayush.easybuycartorderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableFeignClients
public class EasybuyCartOrderServiceApplication {

    static void main(String[] args) {
        SpringApplication.run(EasybuyCartOrderServiceApplication.class, args);
    }

}
