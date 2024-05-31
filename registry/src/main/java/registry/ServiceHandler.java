package registry;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Data
@AllArgsConstructor
public class ServiceHandler implements Runnable {
    private final Socket socket;
    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String requestType;
            while ((requestType = in.readLine()) != null) { // 处理多个请求
                Command command = null;

                if ("display".equals(requestType)){
                    command = new DisplayCommand();
                } else if ("register".equals(requestType)) {
                    command = new RegisterCommand();
                } else if ("query".equals(requestType)) {
                    command = new DiscoverCommand();
                }

                if (command != null) {
                    command.execute(in, out);
                } else {
                    out.println("无效的请求类型！");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
