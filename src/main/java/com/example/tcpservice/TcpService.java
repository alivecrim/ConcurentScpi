package com.example.tcpservice;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class TcpService {
    private final BufferedReader br;
    private final PrintWriter pw;
    private AtomicBoolean abort;


    public TcpService() throws IOException {
        Socket socket = new Socket("127.0.0.1", 10000);
        this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.pw = new PrintWriter(socket.getOutputStream());
        this.abort = new AtomicBoolean(false);
    }

    @SneakyThrows
    public void abort() {
        this.abort.set(true);
        String s = this.processMessageRedundant("*RST");
    }

    public void reset() {
        this.abort.set(false);
    }

    synchronized String processMessageRedundant(String message) throws IOException, ScpiDeviceAbortException {
        if (this.abort.get()) {
            return peekMessage(message);
        } else {
            throw new ScpiDeviceAbortException("Устройство выдачи SCPI заблокировано!");
        }
    }

    synchronized String processMessageNormal(String message) throws IOException, ScpiDeviceAbortException {
        if (!this.abort.get()) {
            return peekMessage(message);
        } else {
            throw new ScpiDeviceAbortException("Устройство выдачи SCPI заблокировано!");
        }
    }

    private String peekMessage(String message) throws IOException {
        pw.println(message);
        pw.flush();
        if (!message.contains("?")) {
            pw.println("*OPC?");
            pw.flush();
        }
        return this.br.readLine();
    }


}
