package registry;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class DisplayCommand implements Command{
    private final ServiceRegistry registry = ServiceRegistry.getInstance();
    @Override
    public void execute(BufferedReader in, PrintWriter out) throws IOException {
        String[] allService = registry.displayService();
        for(String service : allService){
            out.println(service);
        }
        out.println(); // 结束标识符
        System.out.println(" - 接收到客户端的查询请求。已向客户端返回可用服务列表");
    }
}
