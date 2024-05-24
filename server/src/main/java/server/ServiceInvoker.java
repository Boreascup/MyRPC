package server;

import protocol.Request;
import common.utils.ReflectionUtils;
/**
 * 调用server实例的服务类
 * 调用具体服务
 */
public class ServiceInvoker {
    public Object invoke(ServiceInstance service,
                         Request request){
        return ReflectionUtils.invoke(service.getTarget(),
                service.getMethod(),
                request.getParameters());
    }
}
