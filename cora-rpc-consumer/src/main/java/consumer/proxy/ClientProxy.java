package consumer.proxy;

/**
 * @author C0ra1
 * @version 1.0
 */
public interface ClientProxy {
    Object getBean(final Class<?> serviceClass);
}
