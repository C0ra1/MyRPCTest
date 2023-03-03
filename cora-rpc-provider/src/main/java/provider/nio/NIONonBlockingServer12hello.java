package provider.nio;

import method.ByeService;
import method.HelloService;
import org.apache.zookeeper.KeeperException;
import provider.api.ByeServiceImpl;
import provider.api.HelloServiceImpl;
import provider.service_registry.ZkServiceRegistry;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * @author C0ra1
 * @version 1.0
 * 以nio为网络编程框架的服务提供端启动类  加入了zookeeper
 */
@SuppressWarnings({"all"})
public class NIONonBlockingServer12hello {
    // 启动
    public static void start(int PORT) throws IOException {
        start0(PORT);
    }

    // 真正启动逻辑
    private static void start0(int port) {
        //创建对应的服务器端通道
        ServerSocketChannel serverSocketChannel = null;
        Selector selector = null;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            System.out.println("-----------服务提供方启动-------------");
            //开启一个选择器 将自己要
            selector = Selector.open();

            //绑定端口开启
            serverSocketChannel.bind(new InetSocketAddress(port));

            // 添加zk
            ZkServiceRegistry.registerMethod("Hello", "127.0.0.1", port);

            //这里注意 要设置非阻塞   阻塞的话  他会一直等待事件或者是异常抛出的时候才会继续 会浪费cpu
            serverSocketChannel.configureBlocking(false);

            //要先设置非阻塞 再注册  如果时先注册再设置非阻塞会报错
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //真正的业务逻辑 就是下面
        //循环等待客户端的连接和检查事件的发生
        while (true) {
            //1秒钟无事发生的话  就继续
            try {
                assert selector != null;
                if (selector.select(1000) == 0) {
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            //获取所有的对象
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isAcceptable()) {
                    try {
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        System.out.println("连接到消费端" + socketChannel.socket().getRemoteSocketAddress());
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                    } catch (ClosedChannelException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (key.isReadable()) {
                    //对可能是因为连接下线而触发的事件进行处理
                    try {
                        //反向获取管道
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        //进行调用方法并返回
                        //获得信息
                        StringBuilder stringBuilder = new StringBuilder();
                        int read = 1;
                        while (read != 0) {
                            //先清空 防止残留
                            buffer.clear();
                            read = socketChannel.read(buffer);
                            //添加的时候  根据读入的数据进行
                            stringBuilder.append(new String(buffer.array(), 0, read));
                        }
                        //方法号和信息中间有个#进行分割
                        String msg = stringBuilder.toString();
                        ByeService byeService = new ByeServiceImpl();
                        String response = byeService.sayBye(msg);

                        String responseMsg = "收到信息" + msg + "来自" + socketChannel.socket().getRemoteSocketAddress();
                        System.out.println(responseMsg);
                        //将调用方法后获得的信息回显
                        ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8));
                        //写回信息
                        socketChannel.write(responseBuffer);
                    } catch (Exception e) {
                        //进行关闭 并继续执行  取消键的注册 还有关闭管道
                        SocketChannel unConnectChannel = (SocketChannel) key.channel();
                        System.out.println(((unConnectChannel.socket().getRemoteSocketAddress()) + "下线了"));
                        key.cancel();
                    }
                }
                keyIterator.remove();
            }
        }
    }
}

