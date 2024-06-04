package client;

import lombok.extern.slf4j.Slf4j;
import protocol.Peer;
import reflection.ReflectionUtils;
import transport.TransportClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RoundRobinTransportSelector implements TransportSelector{
    private final List<TransportClient> clients;
    private int index;
    public RoundRobinTransportSelector(){
        clients = new ArrayList<>();
        index = 0;
    }
    @Override
    public synchronized void init(List<Peer> peers, int count, Class<? extends TransportClient> clazz) {
        count = Math.max(count, 1);
        for(Peer peer : peers){
            for(int i=0; i<count; i++){
                TransportClient client = ReflectionUtils.newInstance(clazz);
                client.connect(peer);
                clients.add(client);
            }
            //log.info("连接了客户 {}", peer);
        }
    }

    @Override
    public synchronized TransportClient select() {
        TransportClient client = clients.get(index);
        index = (index + 1) % clients.size();
        return client;
    }

    @Override
    public synchronized void release(TransportClient client) {
        clients.add(client);
    }

    @Override
    public synchronized void close() {
        for(TransportClient client : clients){
            client.close();
        }
        clients.clear();
    }
}
