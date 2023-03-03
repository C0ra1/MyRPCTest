package provider.service_registry;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.nio.charset.StandardCharsets;

import static constants.RpcConstants.ZOOKEEPER_ADDRESS;

/**
 * @author C0ra1
 * @version 1.0
 */
@Slf4j
public class ZkCuratorRegistry {
    public static void registerMethod(String rpcServiceName, String hostName, int port) {
        // �������� Ȼ�󽫶�Ӧ�Ķ���ע���ȥ����
        // BackoffRetry �˱ܲ��ԣ�����ʧ�ܺ����ȷ������ֵ��
        // ExponentialBackOffPolicy
        // ָ���˱ܲ��ԣ������ò���sleeper��initialInterval��
        // maxInterval��multiplier��initialIntervalָ����ʼ����ʱ�䣬Ĭ��100���룬
        // maxIntervalָ���������ʱ�䣬Ĭ��30�룬multiplierָ������������һ������ʱ��Ϊ��ǰ����ʱ��*multiplier��

        CuratorFramework client = CuratorFrameworkFactory.newClient(ZOOKEEPER_ADDRESS,
                new ExponentialBackoffRetry(1000, 3));
        //��Ҫ����  ��ע����Ϻ�ǵùر� ��Ȼ���˷�ϵͳ��Դ
        client.start();

        //���д���  �������ж��Ƿ񴴽���service���ж�Ӧ�ķ���·�� �������ж� �����ҿ��ܻ�û����ȫ��͸curator
        //���߳����� һ��Ҫ���м���
        synchronized (ZkCuratorRegistry.class) {
            try {
                if (client.checkExists().forPath("/service") == null) {
                    client.create().forPath("/service", "".getBytes(StandardCharsets.UTF_8));
                }
                if (client.checkExists().forPath("/service/" + rpcServiceName) == null) {
                    client.create().forPath("/service/" + rpcServiceName, "".getBytes(StandardCharsets.UTF_8));
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        String date = hostName + ":" + port;
        try {
            client.create().forPath("/service/" + rpcServiceName + "/" + date, "0".getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        client.close();
        log.info("�����:" + hostName + ":" + port + ":" + rpcServiceName + "������zkCurator��ע�����");
    }
}
