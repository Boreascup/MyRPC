package server;

import lombok.Data;
import serialization.Encoder;
import serialization.Decoder;
import serialization.JsonEncoder;
import serialization.JsonDecoder;
import transport.HTTPServer;
import transport.TransportServer;

@Data
public class RpcServerConfig {
    private Class<? extends TransportServer> transportClass = HTTPServer.class;
    private Class<? extends Encoder> encoderClass = JsonEncoder.class;
    private Class<? extends Decoder> decoderClass = JsonDecoder.class;
    private int port;
    private String ipAddress;

    /**
     *
     * @param port 要监听的端口号
     * @param ipAddress 要监听的ip地址
     */
    public RpcServerConfig(int port, String ipAddress){
        this.port = port;
        this.ipAddress = ipAddress;
    }
}
