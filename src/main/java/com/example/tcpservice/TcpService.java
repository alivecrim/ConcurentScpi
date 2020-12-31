package com.example.tcpservice;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.*;

@Service
public class TcpService {
    private final BufferedReader br;
    private final PrintWriter pw;


    private final ApplicationEventPublisher eventPublisher;
    private ConcurrentHashMap<UUID, CompletableFuture<String>> resultsCf;
    private BlockingQueue<StringUUID> requests;

    public TcpService(ApplicationEventPublisher eventPublisher, Lister lister) throws IOException {
        resultsCf = new ConcurrentHashMap<>();
        lister.setResultsCf(resultsCf);

        this.eventPublisher = eventPublisher;
        requests = new LinkedBlockingQueue<>(20);
        Socket socket = new Socket("127.0.0.1", 10000);
        this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.pw = new PrintWriter(socket.getOutputStream());
        CompletableFuture.runAsync(() -> {
            try {
                initSender();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void initSender() throws InterruptedException, IOException {
        while (true) {
                StringUUID take = this.requests.take();
                this.pw.println(take.getMessage());
                this.pw.flush();
                String res;
                if (take.getMessage().contains("?")) {
                    res = this.br.readLine();
                } else {
                    this.pw.println("*OPC?");
                    this.pw.flush();
                    res = this.br.readLine();
                }
                this.eventPublisher.publishEvent(new TcpServiceResponse(this, res, take.getUuid()));
                this.resultsCf.remove(take.getUuid());
            }
    }


     CompletableFuture<String> processMessage(String message) throws InterruptedException {
        StringUUID stringUUID = new StringUUID(message);
        UUID uuid = stringUUID.getUuid();
        this.requests.offer(stringUUID);
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "timeout";
        });
        resultsCf.put(uuid, cf);
        return cf;
    }
}
