package server;

import lombok.Data;
import serialization.Encoder;
import serialization.Decoder;
import serialization.JsonEncoder;
import serialization.JsonDecoder;
import transport.HTTPTransportServer;
import transport.TransportServer;

@Data
public class RpcServerConfig {
    private Class<? extends TransportServer> transportClass = HTTPTransportServer.class;
    private Class<? extends Encoder> encoderClass = JsonEncoder.class;
    private Class<? extends Decoder> decoderClass = JsonDecoder.class;
    private int port;

    /**
     * @param port 要监听的端口号
     */
    public RpcServerConfig(int port){
        this.port = port;
    }
}
