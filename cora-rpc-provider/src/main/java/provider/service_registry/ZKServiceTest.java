package provider.service_registry;

import constants.RpcConstants;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author C0ra1
 * @version 1.0
 */
public class ZKServiceTest {
    private static String connectString = RpcConstants.ZOOKEEPER_ADDRESS;
    private static int sessionTimeout = RpcConstants.ZOOKEEPER_SESSION_TIMEOUT;
    private static ZooKeeper zooKeeper;

    @Test
    public void connect() throws IOException, InterruptedException, KeeperException {
        zooKeeper = new ZooKeeper(connectString, sessionTimeout, null);
        zooKeeper.create("/test2", "12".getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }
}
