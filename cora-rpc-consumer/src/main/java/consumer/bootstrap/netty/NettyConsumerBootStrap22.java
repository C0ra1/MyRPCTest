package consumer.bootstrap.netty;

import consumer.proxy.ClientProxyTool;
import method.Customer;

/**
 * @author C0ra1
 * @version 1.0
 */

public class NettyConsumerBootStrap22 {
    public static Customer main() {
        ClientProxyTool proxy = new ClientProxyTool();
        return (Customer) proxy.getBean(Customer.class);
    }
}
