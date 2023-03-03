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
 * 该类将对应服务端的方法和相应的端口和地址，注册到zooKeeper中
 */

@Slf4j
public class ZkServiceRegistry {
    private static ZooKeeper zooKeeper;

    //只需要初始化一次 每次都进行 会消耗资源
    static {
        try {
            zooKeeper = new ZooKeeper(ZOOKEEPER_ADDRESS, ZOOKEEPER_SESSION_TIMEOUT, watchedEvent -> {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //创建成功后把方法注册进去
    static void register(String rpcServiceName, String hostName, int port) {
        //节点名就是方法名  然后对应的数据就是hostname+"："+port


        //因为这个地区属于一个临界区 可能会发生线程不安全问题 所以进行上?
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

                //v1.3进行软负载均衡修改
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

        //权限目前都设置为全放开   创建方式均为持久化
        //修改 v1.3  数据为访问次数 应该是可以进行加减的  然后发现服务端取的是最低的然后再进行+1
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
     * @param rpcServiceName 这是对应的服务名
     * @param hostName       和可以调用该服务的ip
     * @param port           还有对应的端口号
     * @throws IOException
     * @throws InterruptedException
     */
    public static void registerMethod(String rpcServiceName, String hostName, int port) {
        register(rpcServiceName, hostName, port);
        log.info("服务端:" + hostName + ":" + port + ":" + rpcServiceName + "方法在zk中注册完毕");
    }
}

