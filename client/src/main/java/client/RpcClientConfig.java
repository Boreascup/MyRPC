package client;


import lombok.Data;
import protocol.Peer;
import serialization.Decoder;
import serialization.Encoder;
import serialization.JsonDecoder;
import serialization.JsonEncoder;
import transport.HTTPClient;
import transport.TransportClient;

import java.util.List;

@Data
public class RpcClientConfig {
    private Class<? extends TransportClient> transportClass = HTTPClient.class;
    private Class<? extends Encoder> encoderClass = JsonEncoder.class;
    private Class<? extends Decoder> decoderClass = JsonDecoder.class;
    private Class<? extends TransportSelector> selectorClass =
            RandomTransportSelector.class;
    private int connectCount = 1;
    private List<Peer> servers;

    public RpcClientConfig(List<Peer> peer){
        this.servers = peer;
    }
}
