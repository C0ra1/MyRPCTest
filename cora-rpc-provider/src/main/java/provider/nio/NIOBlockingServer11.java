package provider.nio;

import entity.RpcRequest;
import method.ByeService;
import method.HelloService;
import provider.api.ByeServiceImpl;
import provider.api.HelloServiceImpl;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * @author C0ra1
 * @version 1.0
 */
@SuppressWarnings({"all"})
public class NIOBlockingServer11 {

    // 启动
    public static void start(int PORT) throws IOException {
        start0(PORT);
    }

    // 真正启动逻辑
    private static void start0(int port) {
        //创建对应的服务器端通道
        ServerSocketChannel serverSocketChannel = null;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            System.out.println("-----------服务提供方启动-------------");
            //阻塞io不需要选择器

            //绑定端口开启
            serverSocketChannel.bind(new InetSocketAddress(port));

            //这里注意 要设置  阻塞的话  他会一直等待事件或者是异常抛出的时候才会继续 会浪费cpu
            serverSocketChannel.configureBlocking(true);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //真正的业务逻辑 就是下面
        //循环等待客户端的连接和检查事件的发生
        while (true) {
            SocketChannel channel = null;
            try {
                assert serverSocketChannel != null;
                channel = serverSocketChannel.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert channel != null;
            System.out.println("来自" + channel.socket().getRemoteSocketAddress() + " 的连接");
            SocketChannel finalChannel = channel;
            new Thread(() -> {
                try {
                    InputStream inputStream = finalChannel.socket().getInputStream();
                    OutputStream outputStream = finalChannel.socket().getOutputStream();
                    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                    while (true) {
                        String response;
                        RpcRequest request = (RpcRequest) objectInputStream.readObject();
                        if (request.getMethodNum() == 1) {
                            HelloService helloService = new HelloServiceImpl();
                            response = helloService.sayHello(request.getMessage());
                        } else if (request.getMethodNum() == 2) {
                            ByeService helloService = new ByeServiceImpl();
                            response = helloService.sayBye(request.getMessage());
                        } else {
                            throw new RuntimeException("传入错误");
                        }
                        System.out.println("收到客户端" + finalChannel.socket().getRemoteSocketAddress() + "的消息" + response);
                        objectOutputStream.writeObject(response);
                    }
                } catch (Exception e) {
                    System.out.println("channel " + finalChannel.socket().getRemoteSocketAddress() + "断开连接");
                    try {
                        finalChannel.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
