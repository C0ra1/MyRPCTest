package consumer.proxy;

import exception.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.util.ServiceLoader;

/**
 * @author C0ra1
 * @version 1.0
 */
@Slf4j
public class SPIClientProxyUtils {
    public static ClientProxy getUtils() {
        ServiceLoader<ClientProxy> loader = ServiceLoader.load(ClientProxy.class);
        for (ClientProxy clientProxy : loader) {
            System.out.println(clientProxy);
            return clientProxy;
        }
        try {
            throw new RpcException("您键入的代理，并未实现，欢迎实现提出pr");
        } catch (RpcException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
