package com.lukaszplawiak.twitterkafkamongoapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class TwitterKafkaMongoAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(TwitterKafkaMongoAppApplication.class, args);
    }
}
