package com.example.tcpservice;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class Lister implements ApplicationListener<TcpServiceResponse> {
    private ConcurrentHashMap<UUID, CompletableFuture<String>> resultsCf;

    public Lister() {
    }

    void setResultsCf(ConcurrentHashMap<UUID, CompletableFuture<String>> resultsCf) {
        this.resultsCf = resultsCf;
    }

    @Override
    public synchronized void onApplicationEvent(TcpServiceResponse event) {
        UUID uuid = event.getUuid();
        CompletableFuture<String> completableFuture = resultsCf.get(uuid);
        completableFuture.complete(event.response);
    }
}
