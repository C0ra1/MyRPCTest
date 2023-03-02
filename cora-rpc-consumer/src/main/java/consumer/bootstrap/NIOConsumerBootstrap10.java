package consumer.bootstrap;

import consumer.nio.NIONonBlockingClient10;

import java.io.IOException;

/**
 * @author C0ra1
 * @version 1.0
 *
 * 以nio为网络编程框架的消费者端启动类
 */
public class NIOConsumerBootstrap10 {
    public static void main(String[] args) throws IOException {
        NIONonBlockingClient10.start("127.0.0.1", 6666);
    }
}
