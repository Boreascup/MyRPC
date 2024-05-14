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
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void start() {
        this.server.start();
    }

    @Override
    public void stop() {
        this.server.stop(0);
    }

    private class RequestHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                InputStream in = exchange.getRequestBody();
                OutputStream out = exchange.getResponseBody();

                if (handler != null) {
                    handler.onRequest(in, out);
                }

                out.flush();
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);  // Send response headers
                out.close();  // Close the output stream before closing the exchange
                exchange.close();
            }
        }
    }

}
