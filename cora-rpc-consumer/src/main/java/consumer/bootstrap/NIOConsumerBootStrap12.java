package consumer.bootstrap;

import consumer.proxy.ClientProxyTool;
import consumer.proxy.nio.RpcNioClientProxy;
import method.Customer;

/**
 * @author C0ra1
 * @version 1.0
 */
public class NIOConsumerBootStrap12 {
    public static void main(String[] args) {
        ClientProxyTool proxy = new ClientProxyTool();
        System.out.println((Customer) proxy.getBean(Customer.class));
    }
}
