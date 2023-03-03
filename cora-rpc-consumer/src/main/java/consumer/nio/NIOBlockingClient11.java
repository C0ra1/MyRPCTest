package consumer.nio;

import entity.RpcRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

/**
 * @author C0ra1
 * @version 1.0
 */
@SuppressWarnings({"all"})
public class NIOBlockingClient11 {
    public static void start(String hostName, int port) {
        start0(hostName, port);
    }

    //������������
    private static void start0(String hostName, int port) {
        //�õ�һ������ͨ��
        SocketChannel socketChannel = null;
        try {
            socketChannel = SocketChannel.open();
            System.out.println("-----------�������ѷ�����-------------");
            //��������
            socketChannel.configureBlocking(true);
            //��������  ��������  ��������Ҫ����������
            socketChannel.connect(new InetSocketAddress(hostName, port));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //������ҵ���߼�  �ȴ������ϵ����� ���з�����Ϣ
        Scanner scanner = new Scanner(System.in);

        //�������ͨ������������

        try {
            assert socketChannel != null;
            ObjectOutputStream outputStream = new ObjectOutputStream(socketChannel.socket().getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socketChannel.socket().getInputStream());
            //���������ȴ� ������ �������� ���ܽ�����һ�� ��Ȼ�ᱨ�쳣
            while (true) {
                int methodNum = scanner.nextInt();
                String message = scanner.next();
                RpcRequest request = new RpcRequest(methodNum, message);
                //�����޶� ʹ�ÿ��Դ��Ͷ��� ͨ���Դ���io������ �������մ���������

                outputStream.writeObject(request);
                System.out.println("��Ϣ����");
                try {
                    String msg = (String) objectInputStream.readObject();
                    System.out.println("�յ����Կͻ��˵���Ϣ" + msg);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert socketChannel != null;
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
