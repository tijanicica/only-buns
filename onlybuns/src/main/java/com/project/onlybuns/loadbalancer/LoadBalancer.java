package com.project.onlybuns.loadbalancer;

import java.util.ArrayList;
import java.util.List;

public class LoadBalancer {
    private List<String> instances; // Lista instanci aplikacije
    private int currentIndex; // Indeks trenutne instance

    public LoadBalancer(List<String> instances) {
        this.instances = new ArrayList<>(instances);
        this.currentIndex = 0;
    }

    // Metoda za dobijanje sledeće instance
    public synchronized String getNextInstance() {
        if (instances.isEmpty()) {
            throw new RuntimeException("No available instances!");
        }
        String instance = instances.get(currentIndex);
        currentIndex = (currentIndex + 1) % instances.size();
        return instance;
    }

    // Oznaka da instanca nije dostupna
    public void markInstanceUnavailable(String instance) {
        instances.remove(instance);
    }

    // Dodavanje nove instance
    public void addInstance(String instance) {
        if (!instances.contains(instance)) {
            instances.add(instance);
        }
    }
}
