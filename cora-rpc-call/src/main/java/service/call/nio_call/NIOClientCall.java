package service.call.nio_call;

import method.Customer;
import service.nio_bootstrap.NIOClientBootStrap;

/**
 * @author C0ra1
 * @version 1.0
 */
public class NIOClientCall {
    public static Customer main(String[] args) {
        return NIOClientBootStrap.start();
    }
}
