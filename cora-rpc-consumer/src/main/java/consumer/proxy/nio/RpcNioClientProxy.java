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

    //��ȡ������� ������ ��ǰ���
    public static Object getBean(final Class<?> serviceClass){
         /*
         �������
         1�����ĸ��������ȥ���ض���
         2����̬��������Ҫʵ�ֵĽӿ� class[]{xxxx.class} �õ��ľ��Ƕ�Ӧ�����
         3����̬������ִ�з�����ʱ����Ҫ�ɵ���
         */
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                        new Class[]{serviceClass},
                        new InvocationHandler() {
                            @Override
                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                //��ʱ��û�����û����������
                                String methodName = method.getName();
                                String response = ZkServiceDiscovery.getStart(methodName, (String) args[0]);
                                return response;
                            }
                        }
                );
    }

}
