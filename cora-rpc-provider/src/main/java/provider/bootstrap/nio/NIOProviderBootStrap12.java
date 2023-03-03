package provider.bootstrap.nio;

import provider.nio.NIONonBlockingServer12bye;
import provider.nio.NIONonBlockingServer12hello;

import java.io.IOException;

/**
 * @author C0ra1
 * @version 1.0
 */
public class NIOProviderBootStrap12 {
    public static void main(String[] args) {
        //����
        new Thread(() -> {
            //��Ϊÿ�������ṩ���ڲ������ڼ���ѭ������ ÿ������һ���߳̽��м���
            try {
                NIONonBlockingServer12hello.start(6666);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            //��Ϊÿ�������ṩ���ڲ������ڼ���ѭ������ ÿ������һ���߳̽��м���
            try {
                NIONonBlockingServer12hello.start(6668);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        //����
        new Thread(() -> {
            try {
                NIONonBlockingServer12bye.start(6667);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
