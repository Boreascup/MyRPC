package registry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

// 具体的发现服务命令
public class DiscoverCommand implements Command {
    private final ServiceRegistry registry = ServiceRegistry.getInstance();

    @Override
    public void execute(BufferedReader in, PrintWriter out) throws IOException {
        String serviceName = in.readLine();
        String serviceAddress = registry.discoverService(serviceName);
        out.println(serviceAddress);

        System.out.println(" - 接收到客户端的查询请求。已向客户端返回服务“" + serviceName + "”的地址信息");
    }
}
