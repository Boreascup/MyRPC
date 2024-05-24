package transport;

import protocol.Peer;

import java.io.InputStream;
import java.net.SocketTimeoutException;

/**
 * 1.创建连接
 * 2.发送数据，并等待响应
 * 3.关闭连接
 */
public interface TransportClient {
    void connect(Peer peer);
    InputStream write(InputStream data) throws SocketTimeoutException;
    void close();
}


