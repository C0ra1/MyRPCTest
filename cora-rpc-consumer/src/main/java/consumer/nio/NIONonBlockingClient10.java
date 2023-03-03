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

    // ����
    public static void start0(String hostName, int port) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        System.out.println("-----------�������ѷ�����-------------");
        socketChannel.configureBlocking(false);
        // ��������
        if (!socketChannel.connect(new InetSocketAddress(hostName, port))) {
            while (!socketChannel.finishConnect());
        }
        // ����ѡ�������������¼�
        Selector selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));

        // �����̼߳������¼�
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    //�����쳣 �������¼�
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
                            //�������ԭ������ ���̳߳���Ӱ��
                            StringBuffer stringBuffer = new StringBuffer();
                            while (read != 0) {
                                buffer.clear();
                                read = channel.read(buffer);
                                stringBuffer.append(new String(buffer.array(),0,read));
                            }
                            System.out.println("�յ�����˻���" + stringBuffer.toString());
                            keyIterator.remove();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        // ������ҵ���߼����ȴ����������룬������Ϣ����
        Scanner scanner = new Scanner(System.in);
        while (true) {
            int methodNum = scanner.nextInt();
            String message = scanner.next();
            String msg = new String(methodNum + "#" + message);
            socketChannel.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
            System.out.println("��Ϣ����");
        }
    }
}
