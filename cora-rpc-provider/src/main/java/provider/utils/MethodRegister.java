package provider.utils;

import annotation.RegistryChosen;
import configuration.GlobalConfiguration;
import exception.RpcException;
import lombok.extern.slf4j.Slf4j;
import provider.service_registry.NacosServiceRegistry;
import provider.service_registry.ZkServiceRegistry;
import provider.service_registry.ZkCuratorRegistry;

/**
 * @author C0ra1
 * @version 1.0
 */
@Slf4j
public class MethodRegister {
    /**
     * ʵ�ʽ���ע��ķ���
     *
     * @param method ��������
     * @param ip     ��Ӧ��ip
     * @param port   ��Ӧ��port
     */
    public static void register(String method, String ip, int port) {

        RegistryChosen annotation = GlobalConfiguration.class.getAnnotation(RegistryChosen.class);

        switch (annotation.registryName()) {
            case "nacos":
                NacosServiceRegistry.registerMethod(method, ip, port);
                break;
            case "zookeeper":
                ZkServiceRegistry.registerMethod(method, ip, port);
                break;
            case "zkCurator":
                ZkCuratorRegistry.registerMethod(method, ip, port);
                break;
            default:
                try {
                    throw new RpcException("�����ڸ�ע������");
                } catch (RpcException e) {
                    log.error(e.getMessage(), e);
                }
        }
    }
}
