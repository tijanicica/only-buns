package com.project.onlybuns.loadbalancer;

import java.net.HttpURLConnection;
import java.net.URL;

public class LoadBalancerClient {
    private LoadBalancer loadBalancer;

    public LoadBalancerClient(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    // Metoda za slanje zahteva
    public String sendRequest(String endpoint) throws Exception {
        int retries = 3; // Maksimalni broj pokušaja
        while (retries > 0) {
            String instance = loadBalancer.getNextInstance(); // Dobij sledeću instancu
            try {
                // Povezivanje sa instancom
                URL url = new URL(instance + endpoint);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                if (connection.getResponseCode() == 200) {
                    return "Success: " + instance;
                }
            } catch (Exception e) {
                System.out.println("Instance unavailable: " + instance);
                loadBalancer.markInstanceUnavailable(instance); // Označi instancu kao nedostupnu
            }
            retries--;
        }
        throw new Exception("All instances are unavailable!");
    }
}
