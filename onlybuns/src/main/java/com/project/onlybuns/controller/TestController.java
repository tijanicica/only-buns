package com.project.onlybuns.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private Environment environment;

    @GetMapping("/api/test")
    public String testInstance() {
        System.out.println("Received request on instance running on port: " + System.getProperty("server.port"));
        return "Response from instance running on port: " + System.getProperty("server.port");
    }
}
