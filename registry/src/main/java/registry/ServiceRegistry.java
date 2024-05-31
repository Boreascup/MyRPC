package registry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class ServiceRegistry {
    private static final ServiceRegistry INSTANCE = new ServiceRegistry();
    private final Map<String, List<String>> serviceRegistry = new ConcurrentHashMap<>();

    private ServiceRegistry() {}

    public static ServiceRegistry getInstance() {
        return INSTANCE;
    }

    public void registerService(String serviceName, String serviceAddress) {
        serviceRegistry.computeIfAbsent(serviceName, k -> new ArrayList<>()).add(serviceAddress);
    }

    public String discoverService(String serviceName) {
        List<String> addresses = serviceRegistry.get(serviceName);
        if (addresses != null && !addresses.isEmpty()) {
            int i = new Random().nextInt(addresses.size()); // 如果有多个地址，会随便挑一个
            return addresses.get(i);
        } else {
            return "未查询到该服务！";
        }
    }
}
