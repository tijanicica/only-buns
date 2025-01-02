package com.project.onlybuns.loadbalancer;

import java.util.Arrays;
import java.util.List;

public class LoadBalancerTest {
    public static void main(String[] args) {
        // Definiši URL-ove instanci aplikacije
        List<String> instances = Arrays.asList("http://localhost:8081", "http://localhost:8080");

        // Inicijalizuj load balancer
        LoadBalancer loadBalancer = new LoadBalancer(instances);
        LoadBalancerClient client = new LoadBalancerClient(loadBalancer);

        // Testiraj raspodelu zahteva
        for (int i = 0; i < 10; i++) {
            try {
                String response = client.sendRequest("/api/test"); // Endpoint zahteva
                System.out.println(response);
            } catch (Exception e) {
                System.out.println("Request failed: " + e.getMessage());
            }
        }
    }
}
