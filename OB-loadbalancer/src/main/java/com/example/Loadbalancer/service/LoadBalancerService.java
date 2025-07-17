package com.example.Loadbalancer.service;

import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
@Service
public class LoadBalancerService {
    private final List<String> backendServers = Arrays.asList(
        "http://localhost:8086",
        "http://localhost:8087"
    );
    private final AtomicInteger counter = new AtomicInteger(0); //mogli smo koristiti obican int

    // Round Robin algoritam
    public String getNextServer() {
        int index = counter.getAndUpdate(i -> (i + 1) % backendServers.size());
        return backendServers.get(index);
    }
}