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
 * ��nioΪ�����̿�ܵķ����ṩ��������  ������zookeeper
 */
@SuppressWarnings({"all"})
public class NIONonBlockingServer12hello {
    // ����
    public static void start(int PORT) throws IOException {
        start0(PORT);
    }

    // ���������߼�
    private static void start0(int port) {
        //������Ӧ�ķ�������ͨ��
        ServerSocketChannel serverSocketChannel = null;
        Selector selector = null;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            System.out.println("-----------�����ṩ������-------------");
            //����һ��ѡ���� ���Լ�Ҫ
            selector = Selector.open();

            //�󶨶˿ڿ���
            serverSocketChannel.bind(new InetSocketAddress(port));

            // ���zk
            ZkServiceRegistry.registerMethod("Hello", "127.0.0.1", port);

            //����ע�� Ҫ���÷�����   �����Ļ�  ����һֱ�ȴ��¼��������쳣�׳���ʱ��Ż���� ���˷�cpu
            serverSocketChannel.configureBlocking(false);

            //Ҫ�����÷����� ��ע��  ���ʱ��ע�������÷������ᱨ��
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //������ҵ���߼� ��������
        //ѭ���ȴ��ͻ��˵����Ӻͼ���¼��ķ���
        while (true) {
            //1�������·����Ļ�  �ͼ���
            try {
                assert selector != null;
                if (selector.select(1000) == 0) {
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            //��ȡ���еĶ���
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isAcceptable()) {
                    try {
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        System.out.println("���ӵ����Ѷ�" + socketChannel.socket().getRemoteSocketAddress());
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                    } catch (ClosedChannelException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (key.isReadable()) {
                    //�Կ�������Ϊ�������߶��������¼����д���
                    try {
                        //�����ȡ�ܵ�
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        //���е��÷���������
                        //�����Ϣ
                        StringBuilder stringBuilder = new StringBuilder();
                        int read = 1;
                        while (read != 0) {
                            //����� ��ֹ����
                            buffer.clear();
                            read = socketChannel.read(buffer);
                            //��ӵ�ʱ��  ���ݶ�������ݽ���
                            stringBuilder.append(new String(buffer.array(), 0, read));
                        }
                        //�����ź���Ϣ�м��и�#���зָ�
                        String msg = stringBuilder.toString();
                        ByeService byeService = new ByeServiceImpl();
                        String response = byeService.sayBye(msg);

                        String responseMsg = "�յ���Ϣ" + msg + "����" + socketChannel.socket().getRemoteSocketAddress();
                        System.out.println(responseMsg);
                        //�����÷������õ���Ϣ����
                        ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8));
                        //д����Ϣ
                        socketChannel.write(responseBuffer);
                    } catch (Exception e) {
                        //���йر� ������ִ��  ȡ������ע�� ���йرչܵ�
                        SocketChannel unConnectChannel = (SocketChannel) key.channel();
                        System.out.println(((unConnectChannel.socket().getRemoteSocketAddress()) + "������"));
                        key.cancel();
                    }
                }
                keyIterator.remove();
            }
        }
    }
}

