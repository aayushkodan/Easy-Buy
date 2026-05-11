package com.aayush.easybuydiscoveryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class EasybuyDiscoveryServiceApplication {

    static void main(String[] args) {
        SpringApplication.run(EasybuyDiscoveryServiceApplication.class, args);
    }

}
