package com.aayush.easybuyuserservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EasybuyUserServiceApplication {

    static void main(String[] args) {
        SpringApplication.run(EasybuyUserServiceApplication.class, args);
    }

}
