package registry;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import protocol.Peer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 维护一个map，key是服务名，value是服务端端口号和ip的自定义类的list
 * 客户端不需要知道服务器地址。客户端知道注册中心的地址和希望调用的服务的名字
 * 向注册中心发出查询请求，注册中心返回服务器的端口和ip(如果有多个，会用到负载均衡来选择一个）
 * 然后剩下的操作是一样的（吧？
 * 考虑多线程问题
 */


@Slf4j
public class RegistryCenter {
    private Map<String, List<Peer>> services = new ConcurrentHashMap<>();

    //注册服务
    public void registerService(String serviceName, Peer serviceInfo) {
        if (!services.containsKey(serviceName)) {
            services.put(serviceName, new ArrayList<>());
        }
        services.get(serviceName).add(serviceInfo);
    }

    // 从注册中心获取服务列表
    public List<Peer> discoverServices(String serviceName) {
        return services.getOrDefault(serviceName, new ArrayList<>());
    }


    public static void main(String[] args) {
        int port = 2024;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Registry center running on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                //TODO
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private static class ClientHandler extends Thread {
//        private Socket socket;
//
//        public ClientHandler(Socket socket) {
//            this.socket = socket;
//        }
//
//        @Override
//        public void run() {
//            try {
//                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//
//                String request = in.readLine();
//                String[] parts = request.split(",");
//                String command = parts[0];
//                String serviceName = parts[1];
//
//                if ("REGISTER".equals(command)) {
//                    String serviceAddress = parts[2];
//                    int servicePort = Integer.parseInt(parts[3]);
//                    services.put(serviceName, new ServiceInfo(serviceAddress, servicePort));
//                    out.println("Service registered successfully");
//                } else if ("DISCOVER".equals(command)) {
//                    ServiceInfo serviceInfo = services.get(serviceName);
//                    if (serviceInfo != null) {
//                        out.println("Service address: " + serviceInfo.getAddress() + ", Service port: " + serviceInfo.getPort());
//                    } else {
//                        out.println("Service not found");
//                    }
//                }
//
//                socket.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
}
