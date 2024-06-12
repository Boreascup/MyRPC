package registry;

import lombok.Getter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegistryCenter {
    // 获取心跳时间
    @Getter
    private static final ConcurrentHashMap<String, Long> serviceHeartbeatMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 2024;
        ServerSocket serverSocket = null;
        ExecutorService executorService = Executors.newFixedThreadPool(20);

        // 启动心跳检测线程
        new Thread(new HeartbeatChecker()).start();

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("------注册中心启动成功------\n\n注册中心运行地址为 " + host + ":" + port + "\n\n-------------------------");

            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    executorService.execute(new ServiceHandler(socket));
                } catch (IOException e) {
                    System.err.println("服务处理异常: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("服务器启动异常: " + e.getMessage());
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    System.err.println("关闭服务器异常: " + e.getMessage());
                }
            }
            executorService.shutdown();
        }
    }

    // 更新心跳时间
    public static void updateHeartbeat(String serviceAddr) {
        serviceHeartbeatMap.put(serviceAddr, System.currentTimeMillis());
    }

}

class HeartbeatChecker implements Runnable {
    private static final long HEARTBEAT_TIMEOUT = 30000; // 30秒

    @Override
    public void run() {
        while (true) {
            long currentTime = System.currentTimeMillis();
            ConcurrentHashMap<String, Long> serviceHeartbeatMap = RegistryCenter.getServiceHeartbeatMap();

            // 使用迭代器遍历并移除超时的服务
            Iterator<Map.Entry<String, Long>> iterator = serviceHeartbeatMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Long> entry = iterator.next();
                String serviceName = entry.getKey();
                long lastHeartbeat = entry.getValue();

                if (currentTime - lastHeartbeat > HEARTBEAT_TIMEOUT) {
                    System.out.println("服务 " + serviceName + " 已超时");
                    iterator.remove(); // 移除超时的服务
                }
            }

            try {
                Thread.sleep(10000); // 每10秒检查一次
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


