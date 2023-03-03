package consumer.bootstrap.netty;

import consumer.netty.NettyClient20;

/**
 * @author C0ra1
 * @version 1.0
 */
public class NettyConsumerBootStrap20 {
    public static void main(String[] args) {
        //在这里 第一参数是ip地址 第二个参数是端口号
        NettyClient20.start(args[0], Integer.parseInt(args[1]));
    }
}