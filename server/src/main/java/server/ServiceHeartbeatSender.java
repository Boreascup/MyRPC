package server;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ServiceHeartbeatSender implements Runnable {
    private final String registryHost;
    private final int registryPort;
    private final String serviceAddr;

    public ServiceHeartbeatSender(String registryHost, int registryPort, String serviceAddr) {
        this.registryHost = registryHost;
        this.registryPort = registryPort;
        this.serviceAddr = serviceAddr;
    }

    @Override
    public void run() {
        while (true) {
            try (Socket socket = new Socket(registryHost, registryPort);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                out.println("heartbeat");
                out.println(serviceAddr);
                System.out.println("发送了心跳信号");

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(10000); // 每10秒发送一次心跳信号
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

