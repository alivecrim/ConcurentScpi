package com.example.tcpservice;

import org.springframework.context.ApplicationEvent;

import java.util.UUID;

public class TcpServiceResponse extends ApplicationEvent {
    String response;
    private UUID uuid;

    public TcpServiceResponse(Object source, String response, UUID uuid) {
        super(source);
        this.response = response;
        this.uuid = uuid;
    }

    public String getResponse() {
        return response;
    }

    public UUID getUuid() {
        return uuid;
    }
}
