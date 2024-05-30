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
                        out.println("服务“" + serviceName + "” 已注册到注册中心");
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
