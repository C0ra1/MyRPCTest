package init;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;

import static constants.RpcConstants.ZOOKEEPER_ADDRESS;
import static constants.RpcConstants.ZOOKEEPER_SESSION_TIMEOUT;

/**
 * @author C0ra1
 * @version 1.0
 * zookeeper ����һ����ʼ���ķ���
 */
@Slf4j
public class ZK {
    private static ZooKeeper zooKeeper;

    public static void init() {
        try {
            zooKeeper = new ZooKeeper(ZOOKEEPER_ADDRESS, ZOOKEEPER_SESSION_TIMEOUT, watchedEvent -> {
            });

            //������ھ�ɾ  �����ھͲ�ɾ
            if (zooKeeper.exists("/service", false) != null) {
                //�ڲ���ʵ�ֵݹ�ɾ��
                deleteAll("/service");
            }
            zooKeeper.close();
        } catch (IOException | KeeperException | InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    //ʵ��ѭ���ݹ�ɾ���ķ���
    private static void deleteAll(String prePath) {
        try {
            List<String> children = zooKeeper.getChildren(prePath, false);
            if (!children.isEmpty()) {
                for (String child : children) {
                    deleteAll(prePath + "/" + child);
                }
            }
            zooKeeper.delete(prePath, -1);
        } catch (KeeperException | InterruptedException e) {
            log.error(e.getMessage(), e); //���������ϢҪ��� �����Ƕ���ȥ ����������д���
        }
    }
}
