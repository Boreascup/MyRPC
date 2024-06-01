package registry;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public String discoverService(String serviceName) {
        List<String> addresses = serviceRegistry.get(serviceName);
        if (addresses != null && !addresses.isEmpty()) {
            int i = new Random().nextInt(addresses.size()); // 如果有多个地址，会随便挑一个
            return addresses.get(i);
        } else {
            return "";
        }
    }

    public String[] displayService(){
        Set<String> keySet = serviceRegistry.keySet();
        String[] serviceName = keySet.toArray(new String[0]);
        if(serviceName.length > 0)
            return serviceName;
        else return new String[]{"注册中心未注册任何服务！"};
    }

}
