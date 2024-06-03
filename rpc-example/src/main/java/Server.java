import server.RpcServer;

import java.util.Scanner;

public class Server {
    public static void main(String[] args) {
        int port;
        String ipAddress;

        Scanner scanner = new Scanner(System.in);

        System.out.println("请输入服务端监听的端口号：");
        port = scanner.nextInt();
        if(port > 65535) throw new IllegalArgumentException("Port value out of range:" + port);
        scanner.nextLine();
        System.out.println("请输入服务端监听的ip地址（默认0.0.0.0）：");
        String inputIpAddress = scanner.nextLine();
        if (inputIpAddress.isEmpty()) {
            ipAddress = "0.0.0.0";
        } else {
            ipAddress = inputIpAddress;
        }


        RpcServer server = new RpcServer(port, ipAddress);
        server. register(MyService.class, new ServiceImpl());
        server.start();
    }
}

//public class Server {
//    public static void main(String[] args) {
//        int port = -1;
//        String ipAddress = "0.0.0.0";
//
//        for (int i = 0; i < args.length; i++)     {
//            switch (args[i]) {
//                case "-p":
//                    if (i + 1 < args.length) {
//                        port = Integer.parseInt(args[++i]);
//                    }
//                    break;
//                case "-h":
//                    System.out.println("帮助：\n-p: 服务器监听的端口号\n-l: 服务器监听的地址");
//                    System.exit(0);
//                    break;
//                case "-l":
//                    if (i + 1 < args.length) {
//                        ipAddress = args[++i];
//                    }
//                    break;
//                default:
//                    System.out.println("未知参数：" + args[i]);
//                    System.exit(1);
//            }
//        }
//
//        if (port < 0) {
//            System.out.println("请输入服务端监听的端口号！");
//            System.exit(1);
//        }
//
//        RpcServer server = new RpcServer(port, ipAddress);
//        server.register(MyService.class, new ServiceImpl());
//        server.start();
//    }
//}

