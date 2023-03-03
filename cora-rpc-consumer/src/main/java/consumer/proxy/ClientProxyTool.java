package consumer.proxy;

import java.util.Objects;

/**
 * @author C0ra1
 * @version 1.0
 */
public class ClientProxyTool implements ClientProxy {
    @Override
    public Object getBean(Class<?> serviceClass) {
        System.out.println(serviceClass);
        System.out.println(SPIClientProxyUtils.getUtils());
        return Objects.requireNonNull(SPIClientProxyUtils.getUtils()).getBean(serviceClass);
    }
}