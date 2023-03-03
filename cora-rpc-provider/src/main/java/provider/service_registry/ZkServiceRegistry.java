package provider.service_registry;

import constants.RpcConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static constants.RpcConstants.ZOOKEEPER_ADDRESS;
import static constants.RpcConstants.ZOOKEEPER_SESSION_TIMEOUT;

/**
 * @author C0ra1
 * @version 1.0
 * ���ཫ��Ӧ����˵ķ�������Ӧ�Ķ˿ں͵�ַ��ע�ᵽzooKeeper��
 */

@Slf4j
public class ZkServiceRegistry {
    private static ZooKeeper zooKeeper;

    //ֻ��Ҫ��ʼ��һ�� ÿ�ζ����� ��������Դ
    static {
        try {
            zooKeeper = new ZooKeeper(ZOOKEEPER_ADDRESS, ZOOKEEPER_SESSION_TIMEOUT, watchedEvent -> {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //�����ɹ���ѷ���ע���ȥ
    static void register(String rpcServiceName, String hostName, int port) {
        //�ڵ������Ƿ�����  Ȼ���Ӧ�����ݾ���hostname+"��"+port


        //��Ϊ�����������һ���ٽ��� ���ܻᷢ���̲߳���ȫ���� ���Խ�����?
        synchronized (ZkServiceRegistry.class) {
            try {
                Stat exists = zooKeeper.exists("/service", false);
                if (exists == null) {
                    zooKeeper.create("/service",
                            "".getBytes(StandardCharsets.UTF_8),
                            ZooDefs.Ids.OPEN_ACL_UNSAFE,
                            CreateMode.PERSISTENT
                    );
                }

                //v1.3�������ؾ����޸�
                exists = zooKeeper.exists("/service/" + rpcServiceName, false);
                if (exists == null) {
                    zooKeeper.create("/service/" + rpcServiceName,
                            "".getBytes(StandardCharsets.UTF_8),
                            ZooDefs.Ids.OPEN_ACL_UNSAFE,
                            CreateMode.PERSISTENT
                    );
                }
            } catch (KeeperException | InterruptedException e) {
                log.error(e.getMessage(), e);
            }

        }

        String date = hostName + ":" + port;

        //Ȩ��Ŀǰ������Ϊȫ�ſ�   ������ʽ��Ϊ�־û�
        //�޸� v1.3  ����Ϊ���ʴ��� Ӧ���ǿ��Խ��мӼ���  Ȼ���ַ����ȡ������͵�Ȼ���ٽ���+1
        try {
            zooKeeper.create("/service/" + rpcServiceName + "/" + date,
                    "0".getBytes(StandardCharsets.UTF_8),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.PERSISTENT
            );
        } catch (KeeperException | InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * @param rpcServiceName ���Ƕ�Ӧ�ķ�����
     * @param hostName       �Ϳ��Ե��ø÷����ip
     * @param port           ���ж�Ӧ�Ķ˿ں�
     * @throws IOException
     * @throws InterruptedException
     */
    public static void registerMethod(String rpcServiceName, String hostName, int port) {
        register(rpcServiceName, hostName, port);
        log.info("�����:" + hostName + ":" + port + ":" + rpcServiceName + "������zk��ע�����");
    }
}

