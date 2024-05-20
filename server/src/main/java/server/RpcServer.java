package server;

import common.utils.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.Request;
import org.Response;
import org.apache.commons.io.IOUtils;
import serialization.Decoder;
import serialization.Encoder;
import transport.RequestHandler;
import transport.TransportServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
public class RpcServer {
    private RpcServerConfig config;//自己的配置
    private TransportServer net;
    private Encoder encoder;
    private Decoder decoder;
    private ServiceManager serviceManager;
    private ServiceInvoker serviceInvoker;
//    private RequestHandler handler = new RequestHandler() {
//        @Override
//        public void onRequest(InputStream receive, OutputStream toResponse) {
//            Response response = new Response();
//            log.info("onRequest开始调用");
//            try{
//                byte[] inBytes = IOUtils.readFully(receive, receive.available());
//                // 打印接收到的原始字节数据
//                //log.info("Received raw data: {}", new String(inBytes, StandardCharsets.UTF_8));
//                Request request = decoder.decode(inBytes, Request.class);
//                log.info("获得请求: {}", request);
//
//                ServiceInstance sis = serviceManager.lookup(request);
//                Object ret = serviceInvoker.invoke(sis, request);
//                log.info("调用后获得数据：{}", ret);
//                response.setData(ret);
//                log.info("设置完数据后的回复: {}", response);
//
//            }catch (Exception e){
//                log.warn(e.getMessage(), e);
//                response.setCode(1);
//                response.setMessage("RPCServer got error: "
//                + e.getClass().getName()
//                + ": " + e.getMessage());
//            }finally {
//                try{
//                    byte[] outBytes = encoder.encode(response);
//                    toResponse.write(outBytes);
//                    log.info("编码后的回复：{}", outBytes);
//                }catch (IOException e){
//                    log.warn(e.getMessage(), e);
//                }
//            }
//        }
//    };

    private RequestHandler handler = new RequestHandler() {
        @Override
        public void onRequest(InputStream receive, OutputStream toResponse) {
            Response response = new Response();
            log.info("onRequest开始调用");
            try {
                byte[] inBytes = IOUtils.readFully(receive, receive.available());
                Request request = decoder.decode(inBytes, Request.class);
                log.info("获得请求: {}", request);

                ServiceInstance sis = serviceManager.lookup(request);
                Object ret = serviceInvoker.invoke(sis, request);
                log.info("调用后获得数据：{}", ret);
                response.setData(ret);
                log.info("设置完数据后的回复: {}", response);

                // 设置响应头
                String responseHeaders = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\n";

                byte[] responseBody = encoder.encode(response);
                responseHeaders += "Content-Length: " + responseBody.length + "\r\n\r\n";
                toResponse.write(responseHeaders.getBytes(StandardCharsets.UTF_8));
                log.info("已设置正常响应头");

                toResponse.write(responseBody);
                log.info("编码后的响应体：{}", new String(responseBody, StandardCharsets.UTF_8));

                toResponse.flush();

            } catch (Exception e) {
                log.warn(e.getMessage(), e);
                response.setCode(1);
                response.setMessage("RPCServer got error: "
                        + e.getClass().getName()
                        + ": " + e.getMessage());

                // 设置错误响应头
                String errorHeaders = "HTTP/1.1 500 Internal Server Error\r\nContent-Type: application/json\r\n\r\n";
                log.info("已设置异常响应头");
                try {
                    toResponse.write(errorHeaders.getBytes(StandardCharsets.UTF_8));
                } catch (IOException ioException) {
                    log.warn(ioException.getMessage(), ioException);
                }
            }
        }
    };


    public<T> void register(Class<T> interfaceClass, T bean){
        this.serviceManager.register(interfaceClass, bean);
    }
    public void start(){
        this.net.start();
    }
    public void stop(){
        this.net.stop();
    }

    public RpcServer(int port){
        this(new RpcServerConfig(port));
    }

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
