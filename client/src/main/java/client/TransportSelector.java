package client;

import protocol.Peer;
import transport.TransportClient;

import java.util.List;

public interface TransportSelector {
    /**
     * 初始化
     * @param peers 可以连接的server端点信息
     * @param count client与server建立多少个连接
     * @param clazz client实现class
     */
    void init(List<Peer> peers, int count, Class<? extends TransportClient> clazz);

    /**
     * 选择一个transport与server做交互
     * @return 网络client
     */
    TransportClient select();

    void release(TransportClient client);

    void close();
}
