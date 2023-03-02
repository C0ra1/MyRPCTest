package consumer.proxy.nio;

import consumer.service_discovery.ZkServiceDiscovery;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;

/**
 * @author C0ra1
 * @version 1.0
 */
public class RpcNioClientProxy {
    static Object rpcClientProxy;

    //获取代理对象 并返回 当前类别
    public static Object getBean(final Class<?> serviceClass){
         /*
         参数详解
         1、用哪个类加载器去加载对象
         2、动态代理类需要实现的接口 class[]{xxxx.class} 得到的就是对应的类别
         3、动态代理类执行方法的时候需要干的事
         */
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                        new Class[]{serviceClass},
                        new InvocationHandler() {
                            @Override
                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                //暂时还没有设置回信这个操作
                                String methodName = method.getName();
                                String response = ZkServiceDiscovery.getStart(methodName, (String) args[0]);
                                return response;
                            }
                        }
                );
    }

}
