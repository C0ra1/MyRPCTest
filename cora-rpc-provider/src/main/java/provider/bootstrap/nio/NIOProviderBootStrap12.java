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
        //启动
        new Thread(() -> {
            //因为每个服务提供端内部都是在监听循环阻塞 每个开启一个线程进行监听
            try {
                NIONonBlockingServer12hello.start(6666);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            //因为每个服务提供端内部都是在监听循环阻塞 每个开启一个线程进行监听
            try {
                NIONonBlockingServer12hello.start(6668);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        //启动
        new Thread(() -> {
            try {
                NIONonBlockingServer12bye.start(6667);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
