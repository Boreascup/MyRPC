import client.RpcClient;
import lombok.extern.slf4j.Slf4j;
import protocol.Peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

@Slf4j
public class Client {
    public static void main(String[] args) {
        /**
         * 向注册中心查询
         */
        String registryHost = "";
        int registryPort = -1;
        String serviceName = "";


//        Scanner scanner = new Scanner(System.in);
//
//        System.out.println("请输入注册中心 ip 地址：");
//        registryHost = String.valueOf(scanner.next());
//        System.out.println("请输入注册中心端口号：");
//        registryPort = scanner.nextInt();
//        System.out.println("请输入要调用的服务名：");
//        serviceName = String.valueOf(scanner.next());

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-p":
                    if (i + 1 < args.length)
                        registryPort = Integer.parseInt(args[++i]);
                    break;
                case "-i":
                    if (i + 1 < args.length)
                        registryHost = args[++i];
                    break;
                case "-n":
                    if (i + 1 < args.length)
                        serviceName = String.valueOf(args[++i]);
                    break;
                case "-h":
                    System.out.println("帮助：\n-p: 注册中心端口号，不得为空\n-i: 注册中心ip地址，不得为空\n-n: 需要调用的服务名称，不得为空\n");
                    System.exit(0);
                    break;
                default:
                    System.out.println("未知参数：" + args[i]);
                    System.exit(1);
            }
        }

        if(registryHost == ""){
            System.out.println("请输入注册中心 ip 地址！");
            System.exit(1);
        }
        if(registryPort < 0){
            System.out.println("请输入注册中心端口号！");
            System.exit(1);
        }
        if(serviceName == ""){
            System.out.println("请输入要调用的服务名！");
            System.exit(1);
        }


        String response;
        String myresponse = null;

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(registryHost, registryPort), 5000);
            socket.setSoTimeout(5000);

            try (PrintWriter out = new  PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println("query"); // 发送查询请求类型
                out.println(serviceName);

                while ((response = in.readLine()) != null) {
                    myresponse = response;
                    System.out.println("调用服务的地址为: " + response);
                }
            }
        } catch (SocketTimeoutException e) {
            System.out.println("连接失败，请检查注册中心是否启动或端口号是否输入错误");
        } catch (IOException e) {
            e.printStackTrace();
        }


        //从注册中心返回信息中 获取端口号和ip地址
        String[] address = new String[0];
        if (myresponse != null) {
            address = myresponse.split(":");
        }
        Peer peer = new Peer(address[0], Integer.parseInt(address[1]));

        RpcClient client = new RpcClient(peer);
        MyService service = client.getProxy(MyService.class);


        switch (serviceName){
            case "sayHello":
                System.out.println(service.sayHello("Ori"));
                break;
            case "sayBye":
                System.out.println(service.sayBye("Ori"));
                break;
            default:
                System.out.println("should not be here");
        }


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

