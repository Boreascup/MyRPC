package server;

import common.utils.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.Request;
import org.Response;
import org.ServiceDescriptor;
import org.apache.commons.io.IOUtils;
import serialization.Decoder;
import serialization.Encoder;
import transport.RequestHandler;
import transport.TransportServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

@Slf4j
public class RpcServer {
    private RpcServerConfig config;//自己的配置
    private TransportServer net;
    private Encoder encoder;
    private Decoder decoder;
    private ServiceManager serviceManager;
    private ServiceInvoker serviceInvoker;
    private RequestHandler handler = new RequestHandler() {
        @Override
        public void onRequest(InputStream receive, OutputStream toResponse) {
            Response response = new Response();
            log.info("onRequest开始调用");
            try{
                byte[] inBytes = IOUtils.readFully(receive, receive.available());
                Request request = decoder.decode(inBytes, Request.class);
                log.info("获得请求: {}", request);

                ServiceInstance sis = serviceManager.lookup(request);
                Object ret = serviceInvoker.invoke(sis, request);
                response.setData(ret);

            }catch (Exception e){
                log.warn(e.getMessage(), e);
                response.setCode(1);
                response.setMessage("RPCServer got error: "
                + e.getClass().getName()
                + ": " + e.getMessage());
            }finally {
                try{
                    byte[] outBytes = encoder.encode(response);
                    toResponse.write(outBytes);
                    log.info("response client");
                }catch (IOException e){
                    log.warn(e.getMessage(), e);
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
