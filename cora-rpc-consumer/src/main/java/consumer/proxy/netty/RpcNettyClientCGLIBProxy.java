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
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author C0ra1
 * @version 1.0
 */
@Slf4j
public class RpcNettyClientCGLIBProxy implements ClientProxy, MethodInterceptor {

    @Override
    public Object getBean(Class serviceClass) {
        //���ö�̬������ǿ��
        Enhancer enhancer = new Enhancer();
        //�����������
        // enhancer.setClassLoader(serviceClass.getClassLoader());
        //���ô�����
        enhancer.setSuperclass(serviceClass);
        //���ö�Ӧ�ķ���ִ���������������ص�
        enhancer.setCallback(this);

        return enhancer.create();
    }

    /**
     * @param obj         ���������ǿ�Ķ���
     * @param method      �����صķ�������Ҫ��ǿ�ķ�����
     * @param args        �������
     * @param methodProxy ���ڵ���ԭʼ����
     */

    @Override //�Զ����Ӧ������ ���ط�����ִ�б������
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) {
        String methodAddress = null;
        try {
            methodAddress = getMethodAddress(method.getName());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        assert methodAddress != null;

        //ÿ�ε���ʱ ���¶�Ӧ�����ĵ��ô����͵��÷���
        RpcMonitorOperator rpcMonitorOperator = new RpcMonitorOperator();
        rpcMonitorOperator.updateServer(methodAddress);

        String[] split = methodAddress.split(":");
        return NettyClient.callMethod(split[0], Integer.parseInt(split[1]), args[0], method);
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
