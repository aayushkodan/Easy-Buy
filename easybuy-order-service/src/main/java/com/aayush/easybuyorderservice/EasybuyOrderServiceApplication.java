package com.aayush.easybuyorderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class EasybuyOrderServiceApplication {

    static void main(String[] args) {
        SpringApplication.run(EasybuyOrderServiceApplication.class, args);
    }

}
