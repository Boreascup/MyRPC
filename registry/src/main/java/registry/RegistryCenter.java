package registry;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RegistryCenter {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 2024;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("注册中心运行地址为 " + host + ":" + port);

            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    new Thread(new ServiceHandler(socket)).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
