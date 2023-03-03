package service.call.netty_call;

import method.Customer;
import service.netty_bootstrap.NettyClientBootStrap;

/**
 * @author C0ra1
 * @version 1.0
 */
public class NettyClientCall {
    public static Customer main(String[] args) {
        return NettyClientBootStrap.start();
    }
}
