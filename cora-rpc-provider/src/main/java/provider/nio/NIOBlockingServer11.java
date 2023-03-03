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

    // ����
    public static void start(int PORT) throws IOException {
        start0(PORT);
    }

    // ���������߼�
    private static void start0(int port) {
        //������Ӧ�ķ�������ͨ��
        ServerSocketChannel serverSocketChannel = null;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            System.out.println("-----------�����ṩ������-------------");
            //����io����Ҫѡ����

            //�󶨶˿ڿ���
            serverSocketChannel.bind(new InetSocketAddress(port));

            //����ע�� Ҫ����  �����Ļ�  ����һֱ�ȴ��¼��������쳣�׳���ʱ��Ż���� ���˷�cpu
            serverSocketChannel.configureBlocking(true);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //������ҵ���߼� ��������
        //ѭ���ȴ��ͻ��˵����Ӻͼ���¼��ķ���
        while (true) {
            SocketChannel channel = null;
            try {
                assert serverSocketChannel != null;
                channel = serverSocketChannel.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert channel != null;
            System.out.println("����" + channel.socket().getRemoteSocketAddress() + " ������");
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
                            throw new RuntimeException("�������");
                        }
                        System.out.println("�յ��ͻ���" + finalChannel.socket().getRemoteSocketAddress() + "����Ϣ" + response);
                        objectOutputStream.writeObject(response);
                    }
                } catch (Exception e) {
                    System.out.println("channel " + finalChannel.socket().getRemoteSocketAddress() + "�Ͽ�����");
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
