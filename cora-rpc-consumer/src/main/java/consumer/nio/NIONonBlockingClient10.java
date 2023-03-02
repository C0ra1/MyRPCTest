package consumer.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @author C0ra1
 * @version 1.0
 */
@SuppressWarnings({"all"})
public class NIONonBlockingClient10 {
    public static void start(String HostName, int Port) throws IOException {
        start0(HostName, Port);
    }

    // 启动
    public static void start0(String hostName, int port) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        System.out.println("-----------服务消费方启动-------------");
        socketChannel.configureBlocking(false);
        // 建立连接
        if (!socketChannel.connect(new InetSocketAddress(hostName, port))) {
            while (!socketChannel.finishConnect());
        }
        // 创建选择器，监听读事件
        Selector selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));

        // 匿名线程监听读事件
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    //捕获异常 监听读事件
                    try {
                        if (selector.select(1000) == 0) {
                            continue;
                        }
                        Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();;
                        while (keyIterator.hasNext()) {
                            SelectionKey key = keyIterator.next();
                            ByteBuffer buffer = (ByteBuffer) key.attachment();
                            SocketChannel channel = (SocketChannel)key.channel();
                            int read = 1;
                            //用这个的原因是怕 多线程出现影响
                            StringBuffer stringBuffer = new StringBuffer();
                            while (read != 0) {
                                buffer.clear();
                                read = channel.read(buffer);
                                stringBuffer.append(new String(buffer.array(),0,read));
                            }
                            System.out.println("收到服务端回信" + stringBuffer.toString());
                            keyIterator.remove();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        // 真正的业务逻辑，等待键盘上输入，进行消息发送
        Scanner scanner = new Scanner(System.in);
        while (true) {
            int methodNum = scanner.nextInt();
            String message = scanner.next();
            String msg = new String(methodNum + "#" + message);
            socketChannel.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
            System.out.println("消息发送");
        }
    }
}
