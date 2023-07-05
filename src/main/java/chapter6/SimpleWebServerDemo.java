package chapter6;

import com.sun.net.httpserver.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.List;

public class SimpleWebServerDemo {

    public static void main(String[] args) throws IOException {
        enhance();
    }

    private static void start() {
        var server = SimpleFileServer.createFileServer(new InetSocketAddress(8080),
                Path.of(System.getProperty("user.dir")), SimpleFileServer.OutputLevel.VERBOSE);
        server.start();
    }

    // 访问 http://localhost:8080/browse/ 可以查看当前目录下的文件
    private static void handler() throws IOException {
        var server = HttpServer.create(new InetSocketAddress(8080),
                10, "/store/", exchange -> {
                    System.out.println(exchange.getRequestURI());
                    exchange.sendResponseHeaders(200, 0);
                    exchange.getResponseBody().close();
                });
        var handler = SimpleFileServer.createFileHandler(Path.of(System.getProperty("user.dir")));
        server.createContext("/browse/", handler);
        server.start();
    }

    private static void enhance() throws IOException {
        // PUT请求使用SomePutHandler
        var h = HttpHandlers.handleOrElse(r -> r.getRequestMethod().equals("GET"),
                exchange -> {
                    System.out.println("handle: " + exchange.getRequestURI());
                    exchange.sendResponseHeaders(200, 0);
                    exchange.getResponseBody().close();
                }, exchange -> {
                    System.out.println("fallback: " + exchange.getRequestURI());
                    exchange.sendResponseHeaders(200, 0);
                    exchange.getResponseBody().close();
                });
        // 向请求header添加Foo:Bar
        var f = Filter.adaptRequest("Add Foo header", r -> r.with("Foo", List.of("Bar")));
        var s = HttpServer.create(new InetSocketAddress(8080), 10, "/", h, f);
        s.start();
    }
}
