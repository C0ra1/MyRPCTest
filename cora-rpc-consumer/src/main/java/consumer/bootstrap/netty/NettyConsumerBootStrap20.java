package consumer.bootstrap.netty;

import consumer.netty.NettyClient20;

/**
 * @author C0ra1
 * @version 1.0
 */
public class NettyConsumerBootStrap20 {
    public static void main(String[] args) {
        //������ ��һ������ip��ַ �ڶ��������Ƕ˿ں�
        NettyClient20.start(args[0], Integer.parseInt(args[1]));
    }
}