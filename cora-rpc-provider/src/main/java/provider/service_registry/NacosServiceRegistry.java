package provider.service_registry;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import constants.RpcConstants;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

/**
 * @author C0ra1
 * @version 1.0
 */
@Slf4j
public class NacosServiceRegistry {
    //ֱ�ӽ���ע��
    public static void registerMethod(String rpcServiceName, String hostname, int port) {
        Properties properties = RpcConstants.propertiesInit();
        try {
            //����namingService
            NamingService namingService = NacosFactory.createNamingService(properties);
            //����ע��
            namingService.registerInstance(rpcServiceName, hostname, port, "DEFAULT");
            log.info("�����:" + hostname + ":" + port + ":" + rpcServiceName + "������nacos��ע�����");
        } catch (NacosException e) {
            log.error(e.getMessage(), e);
        }
    }
}
