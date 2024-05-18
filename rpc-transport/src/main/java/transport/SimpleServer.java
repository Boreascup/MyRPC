package transport;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class SimpleServer implements TransportServer {
    private RequestHandler handler;
    private HttpServer server;

    @Override
    public void init(int port, RequestHandler handler) {
        this.handler = handler;
        try {
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
            this.server.createContext("/", new RequestHttpHandler());
            log.info("初始化成功");
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void start() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                server.start();
                while (!executor.isShutdown()) {
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
        log.info("transport start函数启动成功！");
    }


    @Override
    public void stop() {
        this.server.stop(0);
    }

    private class RequestHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            log.info("handle开始调用");
            if ("POST".equals(exchange.getRequestMethod())) {
                InputStream in = exchange.getRequestBody();
                OutputStream out = exchange.getResponseBody();

                if (handler != null) {
                    handler.onRequest(in, out);
                }
                out.flush();
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                out.close();
                exchange.close();
            }
            log.info("handle处理成功！");
        }
    }

}
