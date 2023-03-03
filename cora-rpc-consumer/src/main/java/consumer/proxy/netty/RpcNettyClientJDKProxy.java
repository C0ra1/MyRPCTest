package consumer.proxy.netty;

import annotation.RegistryChosen;
import configuration.GlobalConfiguration;
import consumer.netty.NettyClient;
import consumer.proxy.ClientProxy;
import consumer.service_discovery.NacosServiceDiscovery;
import consumer.service_discovery.ZkCuratorDiscovery;
import consumer.service_discovery.ZkServiceDiscovery;
import exception.RpcException;
import lombok.extern.slf4j.Slf4j;
import monitor.RpcMonitorOperator;

import java.lang.reflect.Proxy;

/**
 * @author C0ra1
 * @version 1.0
 */
@Slf4j
public class RpcNettyClientJDKProxy implements ClientProxy {
    //���� ������Ҫ�������ɴ��������
    public Object getBean(final Class<?> serviceClass) {
        //���ݶ�Ӧ�ķ��� ��������д���
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{serviceClass},
                (proxy, method, args) -> {
                    String methodName = method.getName();
                    Object param = args[0];
                    //��ȡ��Ӧ�ķ�����ַ
                    String methodAddress = null;
                    try {
                        methodAddress = getMethodAddress(methodName);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                    assert methodAddress != null;

                    //ÿ�ε���ʱ ���¶�Ӧ�����ĵ��ô����͵��÷���
                    RpcMonitorOperator rpcMonitorOperator = new RpcMonitorOperator();
                    rpcMonitorOperator.updateServer(methodAddress);

                    String[] strings = methodAddress.split(":");
                    String hostName = strings[0];
                    int port = Integer.parseInt(strings[1]);
                    //���з����ĵ���  �漴���з����ĵ���
                    return NettyClient.callMethod(hostName, port, param, method);
                });
    }

    /**
     * ʵ��ȥ��ö�Ӧ�ķ��� ����ɷ������õķ���
     *
     * @param methodName ���ݷ�����  ������ӵ�ע������ע����ѡ����Ӧ��ע�����Ľ���  ʵ�ָ��ؾ����ȡһ��������Ӧ��ַ
     */
    private static String getMethodAddress(String methodName) {
        //����ע����з�������
        //�����ڴ������ϵ�ע�����  ��������µ���Ϊ�Ǹ�class���� ����ֱ�Ӽ�����ȡ ע��
        RegistryChosen annotation = GlobalConfiguration.class.getAnnotation(RegistryChosen.class);
        switch (annotation.registryName()) {
            case "nacos":
                return NacosServiceDiscovery.getMethodAddress(methodName);
            case "zookeeper":
                return ZkServiceDiscovery.getMethodAddress(methodName);
            case "zkCurator":
                return ZkCuratorDiscovery.getMethodAddress(methodName);
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
