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
            while ((requestType = in.readLine()) != null) {
                Command command = null;

                switch (requestType) {
                    case "display":
                        command = new DisplayCommand();
                        break;
                    case "register":
                        command = new RegisterCommand();
                        break;
                    case "query":
                        command = new DiscoverCommand();
                        break;
                }

                if (command != null) {
                    command.execute(in, out);
                } else {
                    out.println("无效的请求类型！");
                }
            }

        } catch (IOException e) {
            System.err.println("处理服务请求时出错: " + e.getMessage());
        }
    }
}
