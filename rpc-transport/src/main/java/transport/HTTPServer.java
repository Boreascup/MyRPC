//package transport;
//
//import com.sun.net.httpserver.HttpExchange;
//import com.sun.net.httpserver.HttpHandler;
//import com.sun.net.httpserver.HttpServer;
//import lombok.extern.slf4j.Slf4j;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
//import java.net.InetSocketAddress;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//@Slf4j
//public class SimpleServer implements TransportServer {
//    private RequestHandler handler;
//    private HttpServer server;
//
//    @Override
//    public void init(int port, RequestHandler handler) {
//        this.handler = handler;
//        try {
//            this.server = HttpServer.create(new InetSocketAddress(port), 0);
//            this.server.createContext("/*", new RequestHttpHandler());
//        } catch (IOException e) {
//            throw new IllegalStateException(e);
//        }
//    }
//
//    @Override
//    public void start() {
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        executor.submit(() -> {
//            try {
//                server.start();
//                while (!executor.isShutdown()) {
//                    Thread.sleep(1000);
//                }
//            } catch (Exception e) {
//                log.error(e.getMessage(), e);
//            }
//        });
//        log.info("transport start函数启动成功！");
//    }
//
//
//    @Override
//    public void stop() {
//        this.server.stop(0);
//    }
//
//    private class RequestHttpHandler implements HttpHandler {
//        @Override
//        public void handle(HttpExchange exchange) throws IOException {
//            log.info("handle开始调用");
//            if ("POST".equals(exchange.getRequestMethod())) {
//                InputStream in = exchange.getRequestBody();
//                OutputStream out = exchange.getResponseBody();
//
//                if (handler != null) {
//                    handler.onRequest(in, out);
//                }
//                out.flush();
//                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
//                out.close();
//                exchange.close();
//            }
//            log.info("handle处理成功！");
//        }
//    }
//
//}

package transport;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class HTTPServer implements TransportServer {
    private RequestHandler handler;
    private ServerSocket serverSocket;
    private boolean running;

    @Override
    public void init(int port, RequestHandler handler) {
        this.handler = handler;
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            log.error("初始化失败: {}", e.getMessage());
        }
    }

    @Override
    public void start() {
        if (serverSocket == null) {
            log.error("Server socket为空，初始化失败");
            return;
        }

        running = true;
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleRequest(clientSocket)).start();
                log.info("RPC Server启动成功。");
            } catch (IOException e) {
                log.error("与客户端连接失败: {}", e.getMessage());
            }
        }
    }

    @Override
    public void stop() {
        try {
            running = false;
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            log.error("服务器关闭失败: {}", e.getMessage());
        }
    }

    private String readHeaders(InputStream in) throws IOException {
        StringBuilder headers = new StringBuilder();
        int ch;
        while ((ch = in.read()) != -1) {
            headers.append((char) ch);
            if (headers.toString().endsWith("\r\n\r\n")) {
                break;
            }
        }
        return headers.toString();
    }

    private int getContentLength(String headers) {
        for (String line : headers.split("\r\n")) {
            if (line.startsWith("Content-Length:")) {
                return Integer.parseInt(line.split(":")[1].trim());
            }
        }
        return 0; // 如果没有找到 Content-Length，则返回 0
    }

    private void handleRequest(Socket clientSocket) {
        try (
                InputStream in = clientSocket.getInputStream();
                OutputStream out = clientSocket.getOutputStream()
        ) {
            if (handler != null) {
                // 读取并解析 HTTP 请求头
                String requestHeaders = readHeaders(in);
//                log.info("打印收到的请求头: \n{}", requestHeaders);

                // 读取请求体
                int contentLength = getContentLength(requestHeaders);
                byte[] requestBody = new byte[contentLength];
                in.read(requestBody);

//                // 调试用：打印请求体数据
//                String requestBodyString = new String(requestBody, StandardCharsets.UTF_8);
//                log.info("打印收到的请求体: {}", requestBodyString);

                // 将请求体传递给 handler
                ByteArrayInputStream requestBodyStream = new ByteArrayInputStream(requestBody);
                handler.onRequest(requestBodyStream, out);

            }
            log.info("成功处理请求!");
            out.flush();
        } catch (IOException e) {
            log.error("处理请求失败: {}", e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                log.error("Error closing client socket: {}", e.getMessage());
            }
        }
    }



}

