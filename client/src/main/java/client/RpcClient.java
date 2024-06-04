package client;

import protocol.Peer;
import reflection.ReflectionUtils;
import serialization.Decoder;
import serialization.Encoder;

import java.lang.reflect.Proxy;
import java.util.List;

public class RpcClient {
    private final RpcClientConfig config;
    private final Encoder encoder;
    private final Decoder decoder;
    private final TransportSelector selector;

    public RpcClient(Peer peer){
        this(new RpcClientConfig(peer));
    }

    public RpcClient(List<Peer> peer){
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
