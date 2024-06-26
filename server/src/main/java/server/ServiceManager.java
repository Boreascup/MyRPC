package server;

import lombok.extern.slf4j.Slf4j;
import protocol.Request;
import protocol.ServiceDescriptor;
import reflection.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ServiceManager {
    private Map<ServiceDescriptor, ServiceInstance> services;
    public ServiceManager(){
        this.services = new ConcurrentHashMap<>();
    }

    public<T> void register(Class<T> interfaceClass, T bean){
        Method[] methods = ReflectionUtils.getPublicMethods(interfaceClass);
        for(Method method : methods){
            ServiceInstance serviceInstance = new ServiceInstance(bean, method);
            ServiceDescriptor serviceDescriptor = ServiceDescriptor.from(interfaceClass, method);

            services.put(serviceDescriptor, serviceInstance);
        }
    }
    public ServiceInstance lookup(Request request) {
        ServiceDescriptor serviceDescriptor = request.getService();
        ServiceInstance instance = services.get(serviceDescriptor);
        if (instance == null) {
            log.error("Service not found for request: {}", request);
        }
        return instance;
    }

}
