import client.RpcClient;
import lombok.extern.slf4j.Slf4j;
import protocol.Peer;
import registry.ServiceRegistry;

import java.io.IOException;
import java.util.Scanner;

@Slf4j
public class Client {
    public static void main(String[] args){

        Scanner scanner = new Scanner(System.in);
        String registryHost = "";
        int registryPort = -1;
        String serviceName = "";

        //这一段往下是调试运行代码
        System.out.println("请输入注册中心 ip 地址：");
        registryHost = String.valueOf(scanner.next());
        System.out.println("请输入注册中心端口号：");
        registryPort = scanner.nextInt();
//        System.out.println("请输入要调用的服务名：");
//        serviceName = String.valueOf(scanner.next());
        //这一段往上是调试运行代码


        //这一段往下是参数运行代码
//        for (int i = 0; i < args.length; i++) {
//            switch (args[i]) {
//                case "-p":
//                    if (i + 1 < args.length)
//                        registryPort = Integer.parseInt(args[++i]);
//                    break;
//                case "-i":
//                    if (i + 1 < args.length)
//                        registryHost = args[++i];
//                    break;
//                case "-n":
//                    if (i + 1 < args.length)
//                        serviceName = String.valueOf(args[++i]);
//                    break;
//                case "-h":
//                    System.out.println("帮助：\n-p: 注册中心端口号，不得为空\n-i: 注册中心ip地址，不得为空\n-n: 需要调用的服务名称\n");
//                    System.exit(0);
//                    break;
//                default:
//                    System.out.println("未知参数：" + args[i]);
//                    System.exit(1);
//            }
//        }
        //这一段往上是参数运行代码

        validateArgs(registryHost, registryPort);

        String[] response;
        try {
            response = ServiceRegistry.connect(registryHost, registryPort, serviceName);
        } catch (IOException e) {
            System.err.println("连接到注册中心时异常：" + e.getMessage());
            return;
        }

        String[] address = new String[2];
        if (response[1] != null) {
            serviceName = response[0];
            int lastIndex = response[1].lastIndexOf(":");
            if (lastIndex != -1) {
                address[0] = response[1].substring(0, lastIndex);
                address[1] = response[1].substring(lastIndex + 1);
            }
        }else{
            System.err.println("未找到服务");
            return;
        }

        //用获得的端口号和ip地址开启远程调用
        Peer peer = new Peer(address[0], Integer.parseInt(address[1]));
        RpcClient client = new RpcClient(peer);
        MyService service = client.getProxy(MyService.class);


        System.out.println("\n调用结果：");
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

    public static void validateArgs(String registryHost, int registryPort){
        if(registryHost == ""){
            System.out.println("请输入注册中心 ip 地址！");
            System.exit(1);
        }
        if(registryPort < 0){
            System.out.println("请输入注册中心端口号！");
            System.exit(1);
        }
    }

}

