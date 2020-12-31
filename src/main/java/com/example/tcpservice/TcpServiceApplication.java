package com.example.tcpservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
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
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.tcpService.reset();
            syn(0, 1000, "Abort_thread plus simple_thread3");
        });

        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            syn(0, 4000, "simple_thread4");
        });

        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(2300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(300);
                    System.out.println(this.tcpService.processMessageNormal("*IDN?"));
                } catch (IOException | InterruptedException | ScpiDeviceAbortException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void syn(int start, int stop, String message) {
        boolean error = false;
        for (int i = start; i < stop; i++) {
            try {
                sender(i);
            } catch (ScpiDeviceAbortException e) {
                System.err.println(e.getMessage());
                error = true;
                break;
            }
        }
        if (error) {
            System.err.println("Syn finished with error: " + message);
        } else {
            System.out.println("Syn finished: " + message);

        }
    }

    private synchronized void sender(int i) throws ScpiDeviceAbortException {
        String state1;
        try {
            this.tcpService.processMessageNormal("state:switch4," + i % 37);
            state1 = this.tcpService.processMessageNormal("state:switch4?");
            if (!state1.equals(String.valueOf(i % 37))) {
                System.out.println("Error assert");
            }else{
                System.out.println("OK");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
