package com.gbdpcloud.bayes;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication
@EnableConfigurationProperties
public class BayesApplication {

    public static void main(String[] args) {
        SpringApplication.run(BayesApplication.class, args);
    }

}
