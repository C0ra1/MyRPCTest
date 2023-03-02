package provider.service_registry;

import constants.RpcConstants;
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

public class ZkServiceRegistry {
    private static String connectString = RpcConstants.ZOOKEEPER_ADDRESS;
    private static int sessionTimeout = RpcConstants.ZOOKEEPER_SESSION_TIMEOUT;
    private static ZooKeeper zooKeeper;

//    //ֻ��Ҫ��ʼ��һ�� ÿ�ζ����� ��������Դ
//    static {
//        try {
//            zooKeeper = new ZooKeeper(connectString, sessionTimeout, watchedEvent -> {
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    static void createConnect() throws IOException {
        zooKeeper = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
            }
        });
    }

    //�����ɹ���ѷ���ע���ȥ
    static void register(String RpcServiceName,String hostname,int port) throws
            InterruptedException, KeeperException {
        //�ڵ������Ƿ����� Ȼ���Ӧ�����ݾ���hostname+"��"+port

        //��Ϊ�����������һ���ٽ��� ���ܻᷢ���̲߳���ȫ���� ���Խ�����?
        synchronized (ZkServiceRegistry.class) {
            Stat exists = zooKeeper.exists("/service", null);
            if (exists == null) {
                zooKeeper.create("/service",
                        "".getBytes(StandardCharsets.UTF_8),
                        ZooDefs.Ids.OPEN_ACL_UNSAFE,
                        CreateMode.PERSISTENT
                );
            }
        }

        String date = hostname+":"+port;
        //Ȩ��Ŀǰ������Ϊȫ�ſ� ������ʽ��Ϊ�־û�
        zooKeeper.create("/service/"+RpcServiceName,
                date.getBytes(StandardCharsets.UTF_8),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT
        );
    }

    /**
     * @param RpcServiceName ���Ƕ�Ӧ�ķ�����
     * @param hostname       �Ϳ��Ե��ø÷����ip
     * @param port           ���ж�Ӧ�Ķ˿ں�
     * @throws IOException
     * @throws InterruptedException
    */
    public static void registerMethod(String RpcServiceName,String hostname,int
            port) throws IOException, InterruptedException, KeeperException {
        //�ȴ�����Ӧ�Ķ�zooKeeper���ӿͻ����ٽ�����Ӧ��ע��
        createConnect();
        register(RpcServiceName,hostname,port);
        System.out.println("�����:"+hostname+":"+port+"����ע�����");
    }
}
