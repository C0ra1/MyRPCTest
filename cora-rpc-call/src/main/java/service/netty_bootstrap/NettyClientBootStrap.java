package service.netty_bootstrap;

import annotation.RpcClientBootStrap;
import consumer.bootstrap.netty.NettyConsumerBootStrap20;
import exception.RpcException;
import lombok.extern.slf4j.Slf4j;
import method.Customer;
import service.ClientCall;

/**
 * @author C0ra1
 * @version 1.0
 */
@Slf4j
public class NettyClientBootStrap {
    public static Customer start() {
        return start0();
    }

    private static Customer start0() {

        //��ȡ��Ӧ�İ汾��  Ȼ��ѡȡ��Ӧ�İ汾���е���
        String currentClientVersion = ClientCall.class.getAnnotation(RpcClientBootStrap.class).version();

        switch (currentClientVersion) {
            case "2.0": //2.0���Ǽ�ʵ��Զ�˵��� ����ûʵ��̫�Ǹ�
                NettyConsumerBootStrap20.main(new String[]{"127.0.0.1", String.valueOf(6668)});
                return null;
//            case "2.1":
//                return NettyConsumerBootStrap21.main();
//            case "2.2":
//                return NettyConsumerBootStrap22.main();
//            case "2.4": //����汾�������л����ߵ�ʹ��
//            case "2.5":
//            case "2.6":
//            case "2.7":
//            case "2.8":
//            case "2.9":
//                return NettyConsumerBootStrap24.main();
            default:
                try {
                    throw new RpcException("�ð汾��û���أ���������뷨����˽���ң��������pr");
                } catch (RpcException e) {
                    log.error(e.getMessage(), e);
                    return null;
                }
        }
    }
}
