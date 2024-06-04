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

@Data
@NoArgsConstructor
public class ServiceRegistry {
    @Getter
    private static final ServiceRegistry Instance = new ServiceRegistry();
    private final Map<String, List<String>> serviceRegistry = new ConcurrentHashMap<>();

    public void registerService(String serviceName, String serviceAddress) {
        serviceRegistry.computeIfAbsent(serviceName, k -> new ArrayList<>()).add(serviceAddress);
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

                out.println("query"); // 查询指定的服务
                out.println(serviceName);

                Map<String, List<String>> response = new HashMap<>();
                String received = in.readLine();
                List<String> addressList = Arrays.asList(received.substring(1, received.length()-1).split(", "));
                response.put(serviceName, addressList);

                return response;//没有处理服务不存在的异常！

            }
        } catch (SocketTimeoutException e) {
            System.out.println("连接超时，请检查注册中心是否启动或端口号是否输入错误");
            throw new SocketTimeoutException("连接超时");
        } catch (IllegalStateException e){
            throw new IllegalStateException("服务不存在");
        }
    }

}
