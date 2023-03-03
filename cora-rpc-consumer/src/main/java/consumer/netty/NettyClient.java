package consumer.netty;

import java.lang.reflect.Method;

/**
 * @author C0ra1
 * @version 1.0
 */
public class NettyClient {
    public static Object callMethod(String hostName, int port, Object param, Method method) {
        //用户根据自己想用的版本 打开对应的注解
        // return NettyClient21.callMethod(hostName, port, param,method);
        // return NettyClient22.callMethod(hostName, port, param,method);
        //return NettyClient24.callMethod(hostName, port, param, method);
        return null;
    }
}