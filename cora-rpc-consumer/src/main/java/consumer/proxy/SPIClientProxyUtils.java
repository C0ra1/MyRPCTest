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
            throw new RpcException("������Ĵ�����δʵ�֣���ӭʵ�����pr");
        } catch (RpcException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
