// u controller/LoadBalancerController.java
package com.project.onlybuns.controller;

import com.project.onlybuns.service.LoadBalancerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders; // Novi import
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collections; // Novi import

@RestController
@RequestMapping("/proxy")
public class LoadBalancerController {

    @Autowired
    private LoadBalancerService loadBalancerService;

    @GetMapping("/**")
    public ResponseEntity<String> forward(HttpServletRequest request) {
        String originalEndpoint = request.getRequestURI().substring("/proxy".length());

        // Kreiramo objekat za zaglavlja i kopiramo ih iz originalnog zahteva
        HttpHeaders headers = new HttpHeaders();
        Collections.list(request.getHeaderNames())
                .forEach(headerName -> headers.add(headerName, request.getHeader(headerName)));

        System.out.println("[Proxy Ulaz] Primio zahtev za: " + originalEndpoint);

        // Prosleđujemo i endpoint I ZAGLAVLJA
        return loadBalancerService.forwardRequest(originalEndpoint, headers);
    }
}