package registry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 维护一个map，key是服务名，value是服务端端口号和ip的自定义类的list
 * 客户端不需要知道服务器地址。客户端知道注册中心的地址和希望调用的服务的名字
 * 向注册中心发出查询请求，注册中心返回服务器的端口和ip(如果有多个，会用到负载均衡来选择一个）
 * 然后剩下的操作是一样的
 * 考虑多线程问题
 */


public class RegistryCenter {
    private static final Map<String, List<String>> serviceRegistry = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 2024;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("注册中心运行地址为 " + host + ":" + port);

            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    new Thread(new ServiceHandler(socket)).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static class ServiceHandler implements Runnable {
        private final Socket socket;

        public ServiceHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                String requestType = in.readLine(); // 读取请求类型（"register" 或 "query"）
                if ("register".equals(requestType)) {
                    String serviceName;
                    while ((serviceName = in.readLine()) != null) {
                        String serviceAddress = in.readLine();
                        registerService(serviceName, serviceAddress);
                        out.println("服务“" + serviceName + "” 已注册到注册中心，地址为" + serviceAddress);
                    }
                } else if ("query".equals(requestType)) {
                    String serviceName = in.readLine();
                    List<String> addresses = serviceRegistry.get(serviceName);
                    if (addresses != null && !addresses.isEmpty()) {
                        int i = new Random().nextInt(addresses.size());//如果有多个地址，会随便挑一个
                        out.println(addresses.get(i));
                    } else {
                        out.println("未查询到该服务！");
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        private void registerService(String serviceName, String serviceAddress) {
            serviceRegistry.compute(serviceName, (key, addresses) -> {
                if (addresses == null) {
                    addresses = new ArrayList<>();
                }
                addresses.add(serviceAddress);
                return addresses;
            });
        }
    }

}
