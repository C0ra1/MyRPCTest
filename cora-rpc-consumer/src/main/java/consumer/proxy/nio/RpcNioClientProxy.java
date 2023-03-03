package consumer.proxy.nio;

import annotation.RegistryChosen;
import configuration.GlobalConfiguration;
import consumer.proxy.ClientProxy;
import consumer.service_discovery.NacosServiceDiscovery;
import consumer.service_discovery.ZkCuratorDiscovery;
import consumer.service_discovery.ZkServiceDiscovery;
import exception.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;

/**
 * @author C0ra1
 * @version 1.0
 */
@Slf4j
public class RpcNioClientProxy implements ClientProxy {
    static Object rpcClientProxy;

    //��ȡ������� ������ ��ǰ���
    public Object getBean(final Class<?> serviceClass) {
        /*
            �������
            1�����ĸ��������ȥ���ض���
            2����̬��������Ҫʵ�ֵĽӿ� class[]{xxxx.class} �õ��ľ��Ƕ�Ӧ�����
            3����̬������ִ�з�����ʱ����Ҫ�ɵ���
         */
        if (rpcClientProxy == null) {
            rpcClientProxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[]{serviceClass},
                    (proxy, method, args) -> {
                        //��ʱ��û�����û����������
                        String methodName = method.getName();
                        return getResponse(methodName, (String) args[0]);
                    }
            );
        }
        return rpcClientProxy;
    }

    /**
     * ʵ��ȥ��ö�Ӧ�ķ��� ����ɷ������õķ���
     *
     * @param msg ��Ϣ
     */
    private static String getResponse(String methodName, String msg) throws IOException {
        //����ע����з�������
        //�����ڴ������ϵ�ע�����  ��������µ���Ϊ�Ǹ�class���� ����ֱ�Ӽ�����ȡ ע��
        RegistryChosen annotation = GlobalConfiguration.class.getAnnotation(RegistryChosen.class);
        switch (annotation.registryName()) {
            case "nacos":
                return NacosServiceDiscovery.getStart(methodName, msg);
            case "zookeeper":
                return ZkServiceDiscovery.getStart(methodName, msg);
            case "zkCurator":
                return ZkCuratorDiscovery.getStart(methodName, msg);
            default:
                try {
                    throw new RpcException("�����ڸ�ע������");
                } catch (RpcException e) {
                    log.error(e.getMessage(), e);
                    return null;
                }
        }
    }
}

