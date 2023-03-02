package provider.api;

import method.HelloService;

/**
 * @author C0ra1
 * @version 1.0
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String saying) {
        return "Hello," + saying;
    }
}
