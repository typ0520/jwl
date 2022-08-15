package com.example.helloservlet;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

/**
 * @author tong
 */
public class SimpleHTTPServer {
    public static void main(String[] args) throws Throwable {
        ServerSocket serverSocket = new ServerSocket(8080);
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        for (;;) {
            final Socket socket = serverSocket.accept();
            threadPool.execute(() -> {
                OutputStream os = null;
                InputStream is = null;
                try {
                    is = socket.getInputStream();
                    os = socket.getOutputStream();
                    byte[] bytes = new byte[is.available()];
                    int result = is.read(bytes);
                    if (result != -1)
                        System.out.println(new String(bytes));

                    String body = "<html><body><h1>Hello world!</h1></body></html>";
                    String response = "HTTP/1.1 200 OK\r\n" +
                            "Content-Length: " + body.getBytes().length + "\r\n" +
                            "Content-Type: text/html\r\n" +
                            "\r\n" +
                            body + "\r\n";
                    os.write(response.getBytes());
                    os.flush();
                    socket.shutdownInput();
                    socket.shutdownOutput();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
