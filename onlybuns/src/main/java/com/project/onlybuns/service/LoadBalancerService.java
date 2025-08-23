// u service/LoadBalancerService.java
package com.project.onlybuns.service;

import org.springframework.http.HttpEntity; // Novi import
import org.springframework.http.HttpHeaders; // Novi import
import org.springframework.http.HttpMethod; // Novi import
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Service
public class LoadBalancerService {

    private final List<String> instances = List.of(
            "http://localhost:8081",
            "http://localhost:8082"
    );
    private int currentIndex = 0;
    private final RestTemplate restTemplate = new RestTemplate();

    // Menjamo potpis metode da prima i HttpHeaders
    public ResponseEntity<String> forwardRequest(String endpoint, HttpHeaders headers) {
        int attempts = 0;
        while (attempts < instances.size()) {
            String targetInstance = getNextInstance();
            System.out.println("    [Load Balancer] Pokušaj #" + (attempts + 1) + ": Prosleđujem na -> " + targetInstance + endpoint);

            try {
                // Kreiramo HttpEntity koji sadrži zaglavlja
                HttpEntity<String> entity = new HttpEntity<>(headers);

                // Koristimo restTemplate.exchange koji nam dozvoljava da pošaljemo zaglavlja
                return restTemplate.exchange(
                        targetInstance + endpoint,
                        HttpMethod.GET, // Eksplicitno kažemo da je GET metoda
                        entity,         // Prosleđujemo entity sa zaglavljima
                        String.class    // Očekujemo String kao odgovor
                );

            } catch (Exception ex) {
                System.err.println("    [Load Balancer] GREŠKA: Instanca " + targetInstance + " nedostupna ili je odbila zahtev. Razlog: " + ex.getMessage());
                attempts++;
            }
        }

        System.err.println("    [Load Balancer] SVE INSTANCE NEDOSTUPNE!");
        return ResponseEntity.status(503).body("Service Unavailable: All application instances are down.");
    }

    private synchronized String getNextInstance() {
        String instance = instances.get(currentIndex);
        currentIndex = (currentIndex + 1) % instances.size();
        return instance;
    }
}