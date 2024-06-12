package registry;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static registry.ServiceRegistry.removeTimeOutService;

@Data
@NoArgsConstructor
public class ServiceRegistry {
    @Getter
    private static final ServiceRegistry Instance = new ServiceRegistry();
    private static final Map<String, List<String>> serviceRegistry = new ConcurrentHashMap<>();

    public void registerService(String serviceName, String serviceAddress) {
        serviceRegistry.computeIfAbsent(serviceName, k -> new ArrayList<>()).add(serviceAddress);
    }

    public static void removeTimeOutService(String serviceAddr) {
        Iterator<Map.Entry<String, List<String>>> iterator = serviceRegistry.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, List<String>> entry = iterator.next();
            List<String> addresses = entry.getValue();

            // 使用迭代器删除List中的元素
            addresses.removeIf(address -> address.equals(serviceAddr));

            // 如果List为空，删除对应的Key
            if (addresses.isEmpty()) {
                iterator.remove();
            }
        }
    }

    public List<String> discoverService(String serviceName) {
        List<String> addresses = serviceRegistry.get(serviceName);
        if (addresses != null && !addresses.isEmpty()) {
            return addresses;
        } else {
            return Collections.emptyList();
        }
    }

    public String[] displayService(){
        Set<String> keySet = serviceRegistry.keySet();
        String[] serviceName = keySet.toArray(new String[0]);
        if(serviceName.length > 0)
            return serviceName;
        else return new String[]{"注册中心未注册任何服务！"};
    }


    public static Map<String, List<String>> connect(String registryHost, int registryPort, String serviceName) throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(registryHost, registryPort), 5000);
            socket.setSoTimeout(5000);

            try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                //客户端启动参数不包含服务名的情况
                if ("".equals(serviceName)) {
                    out.println("display"); // 展示可调用服务列表
                    System.out.println("\n---注册中心回复---\n\n[已注册服务列表]");
                    String response;
                    while ((response = in.readLine()) != null && !response.isEmpty()) {
                        System.out.println(" - " + response);
                    }
                    System.out.println("\n------------------");
                    System.out.println("请输入需要调用的服务名称:");
                    Scanner scanner = new Scanner(System.in);
                    serviceName = scanner.nextLine();
                }

                //此时服务名一定不为空
                out.println("query"); // 查询指定的服务
                out.println(serviceName);

                Map<String, List<String>> response = new HashMap<>();
                String received = in.readLine();
                List<String> addressList = Arrays.asList(received.substring(1, received.length()-1).split(", "));
                response.put(serviceName, addressList);

                return response;
            }
        } catch (SocketTimeoutException e) {
            System.out.println("连接超时，请检查注册中心是否启动或端口号是否输入错误");
            throw new SocketTimeoutException("连接超时");
        } catch (IllegalStateException e){
            throw new IllegalStateException("服务不存在");
        }
    }

}


class HeartbeatChecker implements Runnable {
    private static final long HEARTBEAT_TIMEOUT = 30000; // 超时时间为30秒

    @Override
    public void run() {
        while (true) {
            long currentTime = System.currentTimeMillis();
            ConcurrentHashMap<String, Long> serviceHeartbeatMap = RegistryCenter.getServiceHeartbeatMap();

            // 使用迭代器遍历并移除超时的服务
            Iterator<Map.Entry<String, Long>> iterator = serviceHeartbeatMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Long> entry = iterator.next();
                String serviceAddr = entry.getKey();
                long lastHeartbeat = entry.getValue();

                if (currentTime - lastHeartbeat > HEARTBEAT_TIMEOUT) {
                    System.out.println(" - 未检测到部署于" + serviceAddr + "的服务心跳，服务已下线!");
                    iterator.remove(); // 移除超时的服务
                    removeTimeOutService(serviceAddr);
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
