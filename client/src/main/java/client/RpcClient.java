package client;

import common.utils.ReflectionUtils;
import org.Peer;
import serialization.Decoder;
import serialization.Encoder;

import java.lang.reflect.Proxy;

public class RpcClient {
    private RpcClientConfig config;
    private Encoder encoder;
    private Decoder decoder;
    private TransportSelector selector;

    public RpcClient(Peer peer){
        this(new RpcClientConfig(peer));
    }

    public RpcClient(RpcClientConfig rpcClientConfig) {
        this.config = rpcClientConfig;

        this.encoder = ReflectionUtils.newInstance(this.config.getEncoderClass());
        this.decoder = ReflectionUtils.newInstance(this.config.getDecoderClass());
        this.selector = ReflectionUtils.newInstance(this.config.getSelectorClass());

        this.selector.init(
                this.config.getServers(),
                this.config.getConnectCount(),
                this.config.getTransportClass()
        );
    }

    public <T> T getProxy(Class<T> clazz){
        return (T) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{clazz},
                new RemoteInvoker(clazz, encoder, decoder, selector)
        );
    }
}
