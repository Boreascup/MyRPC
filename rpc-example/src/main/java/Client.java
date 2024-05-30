import client.RpcClient;
import protocol.Peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


//public class Client {
//    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//
//        System.out.println("请输入客户端的端口号：");
//        int port = scanner.nextInt();
//        System.out.println("请输入目标服务端 ip 地址：");
//        String host = scanner.next();
//
//        Peer peer = new Peer(host, port);
//
//        // 创建多个客户端线程
//        int numberOfClients = 11; // 可以调整这个数值来增加并发客户端数量
//        for (int i = 0; i < numberOfClients; i++) {
//            new Thread(new ClientTask(peer)).start();
//        }
//    }
//}
//
//class ClientTask implements Runnable {
//    private Peer peer;
//
//    public ClientTask(Peer peer) {
//        this.peer = peer;
//    }
//
//    @Override
//    public void run() {
//        RpcClient client = new RpcClient(peer);
//        MyService service = client.getProxy(MyService.class);
//
//        // 执行服务方法
//        int r1 = service.add(1, 2);
//        int r2 = service.minus(10, 8);
//
//        // 打印结果
//        System.out.println(Thread.currentThread().getName() + ": " + r1);
//        System.out.println(Thread.currentThread().getName() + ": " + r2);
//    }
//}



public class Client {
    public static void main(String[] args) {
        /**
         * 向注册中心查询
         */
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入要调用的服务名：");
        String serviceName = String.valueOf(scanner.next());
        String registryHost = "127.0.0.1"; // 注册中心地址
        int registryPort = 2024; // 注册中心端口

        String response = null;
        String myresponse = null;
        try (Socket socket = new Socket(registryHost, registryPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("query"); // 发送查询请求类型
            out.println(serviceName); // 发送要查询的服务名

            while ((response = in.readLine()) != null) {
                myresponse = response;
                System.out.println("Service address: " + response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

//        System.out.println("请输入客户端的端口号：");
//        int port = scanner.nextInt();
//        System.out.println("请输入目标服务端 ip 地址：");
//        String host = String.valueOf(scanner.next());
        String[] address = myresponse.split(":");
        Peer peer = new Peer(address[0], Integer.parseInt(address[1]));

        RpcClient client = new RpcClient(peer);
        MyService service = client.getProxy(MyService.class);

        int r1 = service.add(1, 2);
        int r2 = service.minus(10, 8);

        System.out.println(r1);
        System.out.println(r2);
    }
}

//public class Client {
//    public static void main(String[] args){
//        int port = 0;
//        String host = "0.0.0.0";
//
//        for (int i = 0; i < args.length; i++) {
//            switch (args[i]) {
//                case "-p":
//                    if (i + 1 < args.length) {
//                        port = Integer.parseInt(args[++i]);
//                    } else {
//                        System.out.println("请输入客户端的端口号");
//                        System.exit(1);
//                    }
//                    break;
//                case "-i":
//                    if (i + 1 < args.length) {
//                        host = args[++i];
//                    } else {
//                        System.out.println("请输入需要发送的服务端 ip 地址");
//                        System.exit(1);
//                    }
//                    break;
//                default:
//                    System.out.println("未知参数：" + args[i]);
//                    System.exit(1);
//            }
//        }
//
//        Peer peer = new Peer(host, port);
//
//        RpcClient client = new RpcClient(peer);
//        MyService service = client.getProxy(MyService.class);
//
//        int r1 = service.add(1, 2);
//        int r2 = service.minus(10, 8);
//
//        System.out.println(r1);
//        System.out.println(r2);
//    }
//}



/**
 * 客户端需要自定义-p和-i
 * -i，客户端需要发送的服务端 ip 地址，需要同时支持 IPv4 和 IPv6，不得为空。
 * -p，客户端需要发送的服务端端口，不得为空。就是在服务端填写的那个端口号！！
 */