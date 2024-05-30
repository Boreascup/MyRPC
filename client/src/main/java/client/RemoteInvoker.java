package client;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import protocol.Request;
import protocol.Response;
import protocol.ServiceDescriptor;
import serialization.Decoder;
import serialization.Encoder;
import transport.TransportClient;

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
    private final Class clazz;
    private final Encoder encoder;
    private final Decoder decoder;
    private final TransportSelector selector;
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
                         Object[] args) {

        Request request = new Request();
        request.setService(ServiceDescriptor.from(clazz, method));
        request.setParameters(args);

        resp = invokeRemote(request);
        if(resp == null || resp.getCode()!=0){
            throw new IllegalStateException("RPC远程调用失败, " + resp.getMessage());
        }
        //log.info("RPC调用成功！");
        return resp.getData();
    }


    private Response invokeRemote(Request request) {
        TransportClient client = null;
        //log.info("RPC正在远程调用");
        try {
            client = selector.select();

            byte[] outBytes = encoder.encode(request);
            //log.info("编码后数值为 {}", outBytes);
            InputStream receive = client.write(new ByteArrayInputStream(outBytes));
            //log.info("client.write已执行");
            byte[] inBytes = IOUtils.readFully(receive, receive.available());
            //log.info("readFully已执行，读取值为{}", inBytes);
            resp = decoder.decode(inBytes, Response.class);
        } catch (IOException e) {
            resp.fail(e.getMessage());
        } finally {
            if (client != null) {
                selector.release(client);
            }
        }
        return resp;
    }

}
