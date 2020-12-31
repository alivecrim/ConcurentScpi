package com.example.tcpservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
        Thread.sleep(1000);

        CompletableFuture.runAsync(()->{
            for (int i = 0; i < 10000; i++) {
                try {
                    this.tcpService.processMessage("state:switch4," + i % 37).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                String state1 = null;
                try {
                    state1 = this.tcpService.processMessage("state:switch4?").get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                System.out.println("Iteration:" + i +" " +state1);

            }
            System.out.println("the end");
        });

        CompletableFuture.runAsync(()->{
            for (int i = 10000; i < 20000; i++) {
                try {
                    this.tcpService.processMessage("state:switch4," + i % 37).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                String state1 = null;
                try {
                    state1 = this.tcpService.processMessage("state:switch4?").get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                System.out.println("Iteration:" + i +" " +state1);

            }
            System.out.println("the end");
        });


    }
}
