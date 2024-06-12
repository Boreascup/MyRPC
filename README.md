### 纯手搓简易RPC框架
- 计算机网络原理大二结课作业
- 编程工具为IDEA，语言为java1.8
- 实现了注册中心、服务端、客户端三大模块
- 实现了消息格式定义、序列化与反序列化（JSON）、服务注册与发现、心跳检测、负载均衡等设计
- 未使用任何第三方框架

#### 代码启动说明
- 注册中心启动
```
\MyRPC\JAR\RegistryJar java -jar Registry.jar
```
- 服务端启动
```
\MyRPC\JAR\ServerJar java -jar Server.jar -l 127.6.1.3 -p 8080

/**
*启动参数说明
*-h:帮助参数
*-l:服务端监听的ip地址（可随意填）
*-p:服务器监听的端口号（可随意填）
*/
```
- 客户端启动
```
\MyRPC\JAR\ClientJar java -jar Client.jar -i 127.0.0.1 -p 2024

/**
*启动参数说明
*-h:帮助参数
*-i:注册中心的ip地址（不可更改）
*-p:注册中心的端口号（不可更改）
*-n:需调用的服务名，可以为空
*/
```

- 设计参考：https://www.imooc.com/learn/1158