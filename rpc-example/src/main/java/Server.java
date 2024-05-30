import reflection.ReflectionUtils;
import server.RpcServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) {
        int port;
        String ipAddress;

        Scanner scanner = new Scanner(System.in);

        System.out.println("请输入服务端监听的端口号：");
        port = scanner.nextInt();

        /**
         * 向注册中心注册服务
         */
        String serviceAddress = "127.0.0.1:" + port; // 服务器运行地址
        String registryHost = "127.0.0.1"; // 注册中心地址
        int registryPort = 2024; // 注册中心端口
        Method[] methods = ReflectionUtils.getPublicMethods(MyService.class);
        String[] methodName = new String[methods.length];
        for (int i = 0; i < methods.length; i++) {
            methodName[i] = methods[i].getName();
        }

        try (Socket socket = new Socket(registryHost, registryPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("register");

            for(int i=0; i<methods.length; i++){
                out.println(methodName[i]);
                out.println(serviceAddress);
                String response = in.readLine();
                System.out.println("Response from registry: " + response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        scanner.nextLine();
        System.out.println("请输入服务端监听的ip地址（默认0.0.0.0）：");
        String inputIpAddress = scanner.nextLine();
        if (inputIpAddress.isEmpty()) {
            ipAddress = "0.0.0.0";
        } else {
            ipAddress = inputIpAddress;
        }

        RpcServer server = new RpcServer(port, ipAddress);
        server.register(MyService.class, new ServiceImpl());
        server.start();
    }
}

//public class Server {
//    public static void main(String[] args) {
//        int port = 0;
//        String host = "0.0.0.0";
//
//        for (int i = 0; i < args.length; i++) {
//            switch (args[i]) {
//                case "-p":
//                    if (i + 1 < args.length) {
//                        port = Integer.parseInt(args[++i]);
//                    } else {
//                        System.out.println("请输入服务端监听的端口号");
//                        System.exit(1);
//                    }
//                    break;
//                case "-h":
//                    System.out.println("帮助：\n-p: 服务器监听的端口号\n-l: 服务器监听的地址");
//                    System.exit(0);
//                    break;
//                case "-l":
//                    if (i + 1 < args.length) {
//                        host = args[++i];
//                    }
//                    break;
//                default:
//                    System.out.println("未知参数：" + args[i]);
//                    System.exit(1);
//            }
//        }
//
//        RpcServer server = new RpcServer(port);
//        server.register(MyService.class, new ServiceImpl());
//        server.start();
//    }
//}

