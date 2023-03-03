package provider.bootstrap.netty;

import provider.netty.NettyServer21;

/**
 * @author C0ra1
 * @version 1.0
 */
public class NettyProviderBootStrap21 {
    static volatile int port = 6666; //��Ӧ�Ķ˿� Ҫ����ȥ ע�ᵽע������ȥ

    public static void main(String[] args) {
        //ֱ�������ｫ��Ӧ�ķ���ʲô�Ľ��зֿ� Ȼ�󴫹�ȥ
        String methods = args[0];
        String nums = args[1];
        String[] methodArray = methods.split(",");
        String[] methodNumArray = nums.split(",");
        //���д���  ���ܻ������ ��ߵĶ˿�
        for (int i = 0; i < methodArray.length; i++) {
            String methodName = methodArray[i];
            for (int methodNum = 0; methodNum < Integer.parseInt(methodNumArray[i]); methodNum++) {
                new Thread(() -> NettyServer21.start(methodName, port++)).start();
            }
        }
    }
}
