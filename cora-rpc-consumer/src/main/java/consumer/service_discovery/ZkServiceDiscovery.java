package consumer.service_discovery;

import constants.RpcConstants;
import consumer.nio.NIONonBlockingClient12;
import exception.RpcException;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

/**
 * @author C0ra1
 * @version 1.0
 */
public class ZkServiceDiscovery {
    private static String connectString = RpcConstants.ZOOKEEPER_ADDRESS;
    private static int sessionTimeout = RpcConstants.ZOOKEEPER_SESSION_TIMEOUT;
    private static ZooKeeper zooKeeper;

    //��һ����Ȼ�����ӵ�Զ�˷���������
    public static void getConnect() throws IOException {
        zooKeeper = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
            }
        });
    }

    // ����������ķ����ַ ��ȡ��Ӧ��Զ�˵�ַ
    public static String getMethodAddress(String methodName) throws RpcException,
            InterruptedException, KeeperException {
        //�жϽڵ����Ƿ���ڶ�Ӧ·�� ���������׳��쳣
        if (zooKeeper.exists("/service/"+methodName,null)==null)
        {
            System.out.println("�����ڸ÷���");
            throw new RpcException();
        }
        //����Ӧ�ڵ��л�ȡ��ַ stat�ڵ�״̬��Ϣ����
        byte[] data = zooKeeper.getData("/service/" + methodName, false,null);
        String address = new String(data);
        return address;
    }

    public static String getStart(String methodName, String msg) throws RpcException, InterruptedException, KeeperException, IOException {
        //��ȡ��Ӧ��Զ�˵�ַ
        String methodAddress = getMethodAddress(methodName);
        //��������
        assert methodAddress != null;
        String[] strings = methodAddress.split(":");
        //����
        String address = strings[0];
        int port = Integer.parseInt(strings[1]);
        return NIONonBlockingClient12.start(address, port, msg);
    }
}
