package com.academicdashboard.backend;

import org.springframework.boot.SpringApplication;

import com.academicdashboard.backend.config.MongoContainerConfiguration;

public class TestBackendApplication {

    public static void main(String[] args) {
        SpringApplication.from(BackendApplication::main)
            .with(MongoContainerConfiguration.class)
            .run(args);
    }
}
