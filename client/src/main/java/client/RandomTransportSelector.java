package client;

import common.utils.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.Peer;
import transport.TransportClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 要让线程安全，所以要加synchronized
 */
@Slf4j
public class RandomTransportSelector implements TransportSelector{
    private List<TransportClient> clients;
    public RandomTransportSelector(){
        clients = new ArrayList<>();
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
            log.info("连接了客户 {}", peer);
        }
    }

    @Override
    public synchronized TransportClient select() {
        int i = new Random().nextInt(clients.size());
        return clients.remove(i);//从clients池里随机取了一个clients返回
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
