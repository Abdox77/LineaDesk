package com.linea_desk.rest_linea;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class RestLineaApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestLineaApplication.class, args);

    }

    @GetMapping("/hello")
    public String getHello() {
        return "hello world from getHello!";
    }
}

