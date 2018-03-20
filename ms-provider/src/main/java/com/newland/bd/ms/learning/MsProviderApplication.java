package com.newland.bd.ms.learning;

import com.netflix.hystrix.HystrixCommandProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

// @EnableTurbine
@SpringBootApplication
@EnableHystrix
public class MsProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsProviderApplication.class, args);
    }
}
