package provider.bootstrap.netty;

import provider.netty.NettyServer21;

/**
 * @author C0ra1
 * @version 1.0
 */
public class NettyProviderBootStrap21 {
    static volatile int port = 6666; //对应的端口 要传过去 注册到注册中心去

    public static void main(String[] args) {
        //直接在这里将对应的方法什么的进行分开 然后传过去
        String methods = args[0];
        String nums = args[1];
        String[] methodArray = methods.split(",");
        String[] methodNumArray = nums.split(",");
        //进行创建  可能会出问题 这边的端口
        for (int i = 0; i < methodArray.length; i++) {
            String methodName = methodArray[i];
            for (int methodNum = 0; methodNum < Integer.parseInt(methodNumArray[i]); methodNum++) {
                new Thread(() -> NettyServer21.start(methodName, port++)).start();
            }
        }
    }
}
