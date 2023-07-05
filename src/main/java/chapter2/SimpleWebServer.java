package chapter2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.concurrent.StructuredTaskScope;

public class SimpleWebServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        try (var serverSocket = new ServerSocket(8080);
             var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            System.out.println("Server started. Listening on port 8080...");

            try {
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    scope.fork(() -> handle(clientSocket));
                }
            } finally {
                scope.shutdown();
                scope.join();
            }
        }
    }

    static Void handle(Socket clientSocket) {
        try {
            System.out.println("Client connected: " + clientSocket.getInetAddress());

            // 读取客户端请求
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String request = reader.readLine();
            System.out.println("Request: " + request);

            Thread.sleep(Duration.ofSeconds(5));

            // 构建响应内容
            String response = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: text/plain\r\n"
                    + "Connection: close\r\n"
                    + "\r\n"
                    + "Hello, World!";

            // 发送响应给客户端
            OutputStream outputStream = clientSocket.getOutputStream();
            outputStream.write(response.getBytes());
            outputStream.flush();

            // 关闭连接
            clientSocket.close();
            System.out.println("Client disconnected.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
