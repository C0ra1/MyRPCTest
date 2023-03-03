package provider.nio;

import lombok.extern.slf4j.Slf4j;
import provider.service_registry.ZkServiceRegistry;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * @author C0ra1
 * @version 1.0
 */
@Slf4j
@SuppressWarnings({"all"})
public class NIONonBlockingServer14 {

    //����
    public static void start(String method, int port) {
        start0(method, port);
    }


    /*
        ����������ҵ���߼�����
        ��Ϊ���Ǽ��װ� ��ô�Ȱ��쳣����ȥ
     */
    private static void start0(String method, int port) {
        ServerSocketChannel serverSocketChannel = null;
        Selector selector = null;
        try {
            //������Ӧ�ķ�������ͨ��
            serverSocketChannel = ServerSocketChannel.open();
            log.info("-----------�����ṩ������-------------");
            //����һ��ѡ���� ���Լ�Ҫ
            selector = Selector.open();

            //�󶨶˿ڿ���
            serverSocketChannel.bind(new InetSocketAddress(port));

            //������ע���zk��
            ZkServiceRegistry.registerMethod(method, "127.0.0.1", port);

            //����ע�� Ҫ���÷�����   �����Ļ�  ����һֱ�ȴ��¼��������쳣�׳���ʱ��Ż���� ���˷�cpu
            serverSocketChannel.configureBlocking(false);

            //Ҫ�����÷����� ��ע��  ���ʱ��ע�������÷������ᱨ��
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        //������ҵ���߼� ��������
        //ѭ���ȴ��ͻ��˵����Ӻͼ���¼��ķ���
        while (true) {
            //1�������·����Ļ�  �ͼ���
            try {
                if (selector.select(1000) == 0) {
                    continue;
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }

            //��ȡ���еĶ���
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isAcceptable()) {
                    try {
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        log.info("���ӵ����Ѷ�" + socketChannel.socket().getRemoteSocketAddress());
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
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

                        String msg = stringBuilder.toString();

                        //v1.4
                        //����Ҫ�����߼��� ���ݻ�õķ����� ȥ�ҵ���Ӧ�ķ���
                        //�������Ǳ����ڹ̶�λ�� ͬʱ���й̶���׺
                        String className = method + "ServiceImpl";
                        Class<?> methodClass = Class.forName("provider.api." + className);
                        //ʵ�� Ҫ��ȡ��Ӧ��ʵ�� �����Ӷ�����ܽ��з���ִ�з���
                        Object instance = methodClass.newInstance();

                        //Ҫ�������������
                        String response = (String) methodClass.
                                getMethod("say" + method, String.class).
                                invoke(instance, msg);
                        String responseMsg = "�յ���Ϣ" + msg + "����" + socketChannel.socket().getRemoteSocketAddress();
                        log.info(responseMsg);
                        //�����÷������õ���Ϣ����
                        ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8));
                        //д����Ϣ
                        socketChannel.write(responseBuffer);
                    } catch (Exception e) {
                        //���йر� ������ִ��  ȡ������ע�� ���йرչܵ�
                        SocketChannel unConnectChannel = (SocketChannel) key.channel();
                        log.info(((unConnectChannel.socket().getRemoteSocketAddress()) + "������"));
                        key.cancel();
                    }
                }
                keyIterator.remove();
            }
        }
    }
}
