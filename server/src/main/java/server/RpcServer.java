package server;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import protocol.Request;
import protocol.Response;
import reflection.ReflectionUtils;
import serialization.Decoder;
import serialization.Encoder;
import transport.RequestHandler;
import transport.TransportServer;

import java.io.*;
import java.lang.reflect.Method;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Slf4j
public class RpcServer {
    private final RpcServerConfig  config;//自己的配置
    private final TransportServer net;
    private final Encoder encoder;
    private final Decoder decoder;
    private final ServiceManager serviceManager;
    private final ServiceInvoker serviceInvoker;

    private final RequestHandler handler = new RequestHandler() {
        @Override
        public void onRequest(InputStream receive, OutputStream toResponse) {
            Response response = new Response();
            //log.info("onRequest开始调用");
            try {
                byte[] inBytes = IOUtils.readFully(receive, receive.available());
                Request request = decoder.decode(inBytes, Request.class);
                //log.info("获得请求: {}", request);

                ServiceInstance sis = serviceManager.lookup(request);
                Object ret = serviceInvoker.invoke(sis, request);
                response.setData(ret);
                //log.info("设置完数据后的回复: {}", response);

                // 设置响应头
                String responseHeaders = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\n";

                byte[] responseBody = encoder.encode(response);
                responseHeaders += "Content-Length: " + responseBody.length + "\r\n\r\n";
                toResponse.write(responseHeaders.getBytes(StandardCharsets.UTF_8));
                //log.info("已设置正常响应头");
                toResponse.write(responseBody);
                //log.info("编码后的响应体：{}", new String(responseBody, StandardCharsets.UTF_8));
                toResponse.flush();

            } catch (Exception e) {
                //log.warn(e.getMessage(), e);
                response.fail("RPCServer发生异常: "
                        + e.getClass().getName()
                        + ": " + e.getMessage());

                // 设置错误响应头
                String errorHeaders = "HTTP/1.1 500 Internal Server Error\r\nContent-Type: application/json\r\n\r\n";
                //log.info("已设置异常响应头");
                try {
                    toResponse.write(errorHeaders.getBytes(StandardCharsets.UTF_8));
                } catch (IOException ioException) {
                    log.warn(ioException.getMessage(), ioException);
                }
            }
        }
    };


    public<T> void register(Class<T> interfaceClass, T bean){
        registerToCenter(interfaceClass);
        this.serviceManager.register(interfaceClass, bean);
    }

    public<T> void registerToCenter(Class<T> interfaceClass){
        String serviceAddress = config.getIpAddress() + ":" + config.getPort(); // 服务器运行地址
        String registryHost = "127.0.0.1"; // 注册中心地址
        int registryPort = 2024; // 注册中心端口
        Method[] methods = ReflectionUtils.getPublicMethods(interfaceClass);
        String[] methodName = new String[methods.length];
        for (int i = 0; i < methods.length; i++) {
            methodName[i] = methods[i].getName();
        }

        try (Socket socket = new Socket(registryHost, registryPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("register");

            for(int i=0; i<methods.length; i++){
                out.println(methodName[i]);
                out.println(serviceAddress);
                String response = in.readLine();
                System.out.println("注册中心回复: " + response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        this.net.start();
    }
    public void stop(){
        this.net.stop();
    }

    public RpcServer(int port, String ipAddress){ this(new RpcServerConfig(port, ipAddress)); }

    public RpcServer(RpcServerConfig config){
        this.config = config;
        this.net = ReflectionUtils.newInstance(config.getTransportClass());
        this.net.init(config.getPort(), this.handler);

        this.decoder = ReflectionUtils.newInstance(config.getDecoderClass());
        this.encoder = ReflectionUtils.newInstance(config.getEncoderClass());
        this.serviceManager = new ServiceManager();
        this.serviceInvoker = new ServiceInvoker();
    }
}
