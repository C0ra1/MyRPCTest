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

    // ����
    public static String start0(String hostName, int port, String msg) {
        //�õ�һ������ͨ��
        Selector selector = null;
        SocketChannel socketChannel = null;

        try {
            socketChannel = SocketChannel.open();
            System.out.println("-----------�������ѷ�����-------------");
            socketChannel.configureBlocking(false);
            // ��������
            if (!socketChannel.connect(new InetSocketAddress(hostName, port))) {
                while (!socketChannel.finishConnect());
            }
            // ����ѡ�������������¼�
            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));

            //���з��� ����̫���� �������յ�
            socketChannel.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        while (true) {
            //�����쳣 �������¼�
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
                    //�������ԭ������ ���̳߳���Ӱ��
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
