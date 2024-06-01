package registry;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RegistryCenter {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 2024;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("------注册中心启动成功------\n\n注册中心运行地址为 " + host + ":" + port + "\n\n-------------------------");

            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    new Thread(new ServiceHandler(socket)).start();
                } catch (IOException e) {
                    System.err.println("服务处理异常: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("服务器启动异常: " + e.getMessage());
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    System.err.println("关闭服务器异常: " + e.getMessage());
                }
            }
        }
    }
}

