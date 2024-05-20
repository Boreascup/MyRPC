package client;


import lombok.Data;
import org.Peer;
import serialization.Decoder;
import serialization.Encoder;
import serialization.JsonDecoder;
import serialization.JsonEncoder;
import transport.HTTPTransportClient;
import transport.SimpleTransportClient;
import transport.TransportClient;

import java.util.ArrayList;
import java.util.List;

@Data
public class RpcClientConfig {
    //private Class<? extends TransportClient> transportClass = HTTPTransportClient.class;
    private Class<? extends TransportClient> transportClass = SimpleTransportClient.class;
    private Class<? extends Encoder> encoderClass = JsonEncoder.class;
    private Class<? extends Decoder> decoderClass = JsonDecoder.class;
    private Class<? extends TransportSelector> selectorClass =
            RoundRobinTransportSelector.class;
    private int connectCount = 1;
    private List<Peer> servers = new ArrayList<>();;

    public RpcClientConfig(Peer peer){
        this.servers.add(peer);
    }
}
