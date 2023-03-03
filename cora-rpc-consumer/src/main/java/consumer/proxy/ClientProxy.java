package consumer.proxy;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author C0ra1
 * @version 1.0
 */
public interface ClientProxy {
    Object getBean(final Class<?> serviceClass);
}
