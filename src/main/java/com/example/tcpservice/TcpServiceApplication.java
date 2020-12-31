package com.example.tcpservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@SpringBootApplication
public class TcpServiceApplication implements CommandLineRunner {
    public final TcpService tcpService;

    public TcpServiceApplication(TcpService tcpService) {
        this.tcpService = tcpService;
    }

    public static void main(String[] args) {
        SpringApplication.run(TcpServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        CompletableFuture.runAsync(() -> {
            syn(0, 10000, "simple_thread 1");
        });
        CompletableFuture.runAsync(() -> {
            syn(10000, 20000, "simple_thread 2");
        });
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.tcpService.abort();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            syn(0, 10000, "Abort_thread plus simple_thread3");
        });

        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            syn(0, 10000, "simple_thread4");
        });


    }

    private void syn(int start, int stop, String message) {
        this.tcpService.reset();
        for (int i = start; i < stop; i++) {
            try {
                sender(i);
            } catch (ScpiDeviceAbortException e) {
                System.out.println(e.getMessage());
                break;
            }
        }
        System.out.println("Syn finished: " + message);
    }

    private synchronized void sender(int i) throws ScpiDeviceAbortException {
        Optional<String> state1;
        try {
            this.tcpService.processMessage("state:switch4," + i % 37);
            state1 = this.tcpService.processMessage("state:switch4?");
            if (!state1.get().equals(String.valueOf(i % 37))) {
                System.out.println("Error assert");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
