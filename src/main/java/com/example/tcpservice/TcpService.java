package com.example.tcpservice;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;
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

    public void abort() {
        this.abort.set(true);
    }

    public void reset() {
        this.abort.set(false);
    }

    synchronized Optional<String> processMessage(String message) throws IOException, ScpiDeviceAbortException {
        if (!this.abort.get()) {
            pw.println(message);
            pw.flush();
            if (!message.contains("?")) {
                pw.println("*OPC?");
                pw.flush();
            }
            return Optional.of(this.br.readLine());
        } else {
            throw new ScpiDeviceAbortException("Передача сообщения не удалась");
        }
    }


}
