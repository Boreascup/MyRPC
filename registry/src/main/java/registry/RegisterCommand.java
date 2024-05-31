package registry;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

// 具体的注册服务命令
public class RegisterCommand implements Command {
    private final ServiceRegistry registry = ServiceRegistry.getInstance();

    @Override
    public void execute(BufferedReader in, PrintWriter out) throws IOException {
        String serviceName;
        while ((serviceName = in.readLine()) != null) {
            String serviceAddress = in.readLine();
            registry.registerService(serviceName, serviceAddress);
            out.println("服务“" + serviceName + "” 已注册到注册中心");
        }
    }
}
