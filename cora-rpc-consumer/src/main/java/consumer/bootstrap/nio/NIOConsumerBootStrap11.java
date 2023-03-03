package consumer.bootstrap.nio;

import consumer.nio.NIOBlockingClient11;

/**
 * @author C0ra1
 * @version 1.0
 */
public class NIOConsumerBootStrap11 {
    public static void main(String[] args) {
        //×èÈûÆô¶¯
        NIOBlockingClient11.start("127.0.0.1", 6666);
    }
}
