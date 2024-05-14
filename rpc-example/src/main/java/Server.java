import server.RpcServer;

import java.util.Scanner;

public class Server {
    public static void main(String[] args) {
        int port = 0;
        String host = "0.0.0.0";

        Scanner scanner = new Scanner(System.in);

        System.out.println("请输入服务端监听的端口号：");//这个端口号是服务端自己的。服务端只需要自己监听自己，等待调用就可以了。
        port = scanner.nextInt();

        RpcServer server = new RpcServer(port);
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

