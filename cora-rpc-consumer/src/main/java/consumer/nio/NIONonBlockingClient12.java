package consumer.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
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
public class NIONonBlockingClient12 {
    public static String start(String HostName, int Port, String msg) throws IOException {
        return start0(HostName, Port, msg);
    }

    // 启动
    public static String start0(String hostName, int port, String msg) {
        //得到一个网络通道
        Selector selector = null;
        SocketChannel socketChannel = null;

        try {
            socketChannel = SocketChannel.open();
            System.out.println("-----------服务消费方启动-------------");
            socketChannel.configureBlocking(false);
            // 建立连接
            if (!socketChannel.connect(new InetSocketAddress(hostName, port))) {
                while (!socketChannel.finishConnect());
            }
            // 创建选择器，监听读事件
            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));

            //进行发送 发的太快了 来不及收到
            socketChannel.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        while (true) {
            //捕获异常 监听读事件
            try {
                if (selector.select(1000) == 0) {
                    continue;
                }
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                ;
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    SocketChannel channel = (SocketChannel) key.channel();
                    int read = 1;
                    //用这个的原因是怕 多线程出现影响
                    StringBuilder stringBuilder = new StringBuilder();
                    while (read != 0) {
                        buffer.clear();
                        read = channel.read(buffer);
                        stringBuilder.append(new String(buffer.array(), 0, read));
                    }
                    return stringBuilder.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
