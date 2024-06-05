package client;

import lombok.extern.slf4j.Slf4j;
import protocol.Peer;
import reflection.ReflectionUtils;
import transport.TransportClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
public class RandomTransportSelector implements TransportSelector{
    private final List<TransportClient> clients;
    private final Random random;

    public RandomTransportSelector(){
        clients = new ArrayList<>();
        random = new Random();
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
        int index = random.nextInt(clients.size());
        return clients.get(index);
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
