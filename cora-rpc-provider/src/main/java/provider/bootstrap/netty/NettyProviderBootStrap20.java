package provider.bootstrap.netty;

import provider.netty.NettyServer20;

/**
 * @author C0ra1
 * @version 1.0
 */
public class NettyProviderBootStrap20 {
    public static void main(String[] args) {
        //����Ҫ�󶨵�ip�Ͷ˿�
        NettyServer20.start(args[0], Integer.parseInt(args[1]));
    }
}
