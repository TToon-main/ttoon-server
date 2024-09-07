package com.server.ttoon;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Service;

//@OpenAPIDefinition(servers = {@Server(url = "https://ttoon.site", description = "Default Server url")})
@SpringBootApplication
@EnableJpaAuditing
public class TToonApplication {

    public static void main(String[] args) {
        SpringApplication.run(TToonApplication.class, args);
    }

}
