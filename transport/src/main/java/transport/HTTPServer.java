package transport;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class HTTPServer implements TransportServer {
    private RequestHandler handler;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private boolean running;

    @Override
    public void init(int port, RequestHandler handler) {
        this.handler = handler;
        try {
            threadPool = Executors.newFixedThreadPool(10);
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            log.error("初始化失败: {}", e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void start() {
        if (serverSocket == null) {
            log.error("serverSocket为空，初始化失败");
            return;
        }

        running = true;
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                threadPool.execute(() -> handleRequest(clientSocket)); // 提交任务到线程池
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

                String requestHeaders = readHeaders(in);
                int contentLength = getContentLength(requestHeaders);
                byte[] requestBody = new byte[contentLength];
                in.read(requestBody);

//                // 调试用：打印请求体数据
//                String requestBodyString = new String(requestBody, StandardCharsets.UTF_8);
//                log.info("打印收到的请求体: {}", requestBodyString);

                ByteArrayInputStream requestBodyStream = new ByteArrayInputStream(requestBody);
                handler.onRequest(requestBodyStream, out);

            }
            //log.info("成功处理远程调用请求!");
            out.flush();
        } catch (IOException e) {
            log.error("处理请求失败: {}", e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                log.error("关闭连接时出错：{}", e.getMessage());
            }
        }
    }
}

