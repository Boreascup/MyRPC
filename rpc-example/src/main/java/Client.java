import client.RpcClient;
import org.Peer;

import java.util.Scanner;

//public class Client {
//    public static void main(String[] args){
//        Scanner scanner = new Scanner(System.in);
//
//        System.out.println("请输入客户端的端口号：");
//        int port = scanner.nextInt();
//        System.out.println("请输入需要发送的服务端 ip 地址：");
//        String host = String.valueOf(scanner.next());
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

public class Client {
    public static void main(String[] args){
        int port = 0;
        String host = "0.0.0.0";

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-p":
                    if (i + 1 < args.length) {
                        port = Integer.parseInt(args[++i]);
                    } else {
                        System.out.println("请输入客户端的端口号");
                        System.exit(1);
                    }
                    break;
                case "-i":
                    if (i + 1 < args.length) {
                        host = args[++i];
                    } else {
                        System.out.println("请输入需要发送的服务端 ip 地址");
                        System.exit(1);
                    }
                    break;
                default:
                    System.out.println("未知参数：" + args[i]);
                    System.exit(1);
            }
        }

        Peer peer = new Peer(host, port);

        RpcClient client = new RpcClient(peer);
        MyService service = client.getProxy(MyService.class);

        int r1 = service.add(1, 2);
        int r2 = service.minus(10, 8);

        System.out.println(r1);
        System.out.println(r2);
    }
}



/**
 * 客户端需要自定义-p和-i
 * -i，客户端需要发送的服务端 ip 地址，需要同时支持 IPv4 和 IPv6，不得为空。
 * -p，客户端需要发送的服务端端口，不得为空。就是在服务端填写的那个端口号！！
 */