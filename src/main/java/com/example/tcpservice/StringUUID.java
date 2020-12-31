package com.example.tcpservice;

import java.util.UUID;

public class StringUUID {
    String message;
    UUID uuid;

    public String getMessage() {
        return message;
    }

    public UUID getUuid() {
        return uuid;
    }

    public StringUUID(String message) {
        this.uuid = UUID.randomUUID();
        this.message = message;
    }
}
