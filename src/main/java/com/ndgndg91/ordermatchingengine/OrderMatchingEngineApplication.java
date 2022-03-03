package com.ndgndg91.ordermatchingengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class OrderMatchingEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderMatchingEngineApplication.class, args);
    }

}