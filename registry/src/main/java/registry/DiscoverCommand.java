package registry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

// 具体的发现服务命令
public class DiscoverCommand implements Command {
    private final ServiceRegistry registry = ServiceRegistry.getInstance();

    @Override
    public void execute(BufferedReader in, PrintWriter out) throws IOException {
        System.out.println("收到了query请求");
        String serviceName = in.readLine();
        String serviceAddress = registry.discoverService(serviceName);
        out.println(serviceAddress);
    }
}
