package com.aayush.easybuyconfigserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class EasybuyConfigServerApplication {

    static void main(String[] args) {
        SpringApplication.run(EasybuyConfigServerApplication.class, args);
    }

}
