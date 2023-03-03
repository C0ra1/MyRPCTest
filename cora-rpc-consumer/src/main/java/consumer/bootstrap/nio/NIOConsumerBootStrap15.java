package consumer.bootstrap.nio;

import consumer.proxy.ClientProxyTool;
import method.Customer;

/**
 * @author C0ra1
 * @version 1.0
 */
public class NIOConsumerBootStrap15 {
    public static Customer main(String[] args) {
        ClientProxyTool proxy = new ClientProxyTool();
        return (Customer) proxy.getBean(Customer.class);
    }
}
