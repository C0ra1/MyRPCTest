package provider.bootstrap.nio;

import provider.nio.NIONonBlockingServer15;

/**
 * @author C0ra1
 * @version 1.0
 */
public class NIOProviderBootStrap15 {
    static volatile int port = 6666;

    public static void main(String[] args) {
        //��Ӧ�ķ����Ͷ�Ӧ�ķ�������Ҫ�������� �����Ķ˿ڲ�һ�� ����д�� ������
        String methodStr = args[0];
        String numStr = args[1];
        String[] methods = methodStr.split(",");
        String[] nums = numStr.split(",");
        //���д���  ���ܻ������ ��ߵĶ˿�
        for (int i = 0; i < methods.length; i++) {
            String methodName = methods[i];
            for (int methodNum = 0; methodNum < Integer.parseInt(nums[i]); methodNum++) {
                new Thread(() -> NIONonBlockingServer15.start(methodName, port++)).start();
            }
        }
    }
}

