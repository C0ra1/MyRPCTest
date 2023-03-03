package consumer.proxy;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author C0ra1
 * @version 1.0
 */
public interface ClientProxy {
    Object getBean(final Class<?> serviceClass);

    //自定义对应的拦截 拦截方法并执行别的任务
    Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy);
}
