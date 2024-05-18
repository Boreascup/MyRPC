package client;

import lombok.extern.slf4j.Slf4j;
import org.Request;
import org.Response;
import org.ServiceDescriptor;
import org.apache.commons.io.IOUtils;
import serialization.*;
import transport.TransportClient;

import java.awt.image.RescaleOp;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 调用远程服务的代理类
 */
@Slf4j
public class RemoteInvoker implements InvocationHandler {
    private Class clazz;
    private Encoder encoder;
    private Decoder decoder;
    private TransportSelector selector;
    private Response resp;
    RemoteInvoker(Class clazz,
                  Encoder encoder,
                  Decoder decoder,
                  TransportSelector selector){
        this.clazz = clazz;
        this.encoder = encoder;
        this.decoder = decoder;
        this.selector = selector;
        this.resp = new Response();
    }

    @Override
    public Object invoke(Object proxy,
                         Method method,
                         Object[] args) throws Throwable {

        Request request = new Request();
        request.setService(ServiceDescriptor.from(clazz, method));
        request.setParameters(args);

        resp = invokeRemote(request);
        if(resp == null || resp.getCode()!=0){
            throw new IllegalStateException("fail to invoke remote: {}" + resp);
        }
        log.info("invoke remote成功！");
        return resp.getData();
    }

    private Response invokeRemote(Request request) {
        TransportClient client = null;
        log.info("远程编码解码开始！");
        try {
            client = selector.select();

            byte[] outBytes = encoder.encode(request);
            log.info("编码后数值为 {}", outBytes);
            try (InputStream receive = client.write(new ByteArrayInputStream(outBytes))) {
                log.info("client.write已执行");
                byte[] inBytes = IOUtils.readFully(receive, receive.available());
                log.info("readFully已执行，读取值为{}", inBytes);
                resp = decoder.decode(inBytes, Response.class);
            }

        } catch (IOException e) {
            log.warn(e.getMessage(), e);
            resp.setCode(1);
            resp.setMessage("RpcClient got error: " + e.getClass() + ":" + e.getMessage());
        } finally {
            if (client != null) {
                selector.release(client);
            }
        }
        log.info("远程编码解码成功！");
        return resp;
    }

}
