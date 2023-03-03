package service.nio_bootstrap;

import annotation.RpcClientBootStrap;
import consumer.bootstrap.NIOConsumerBootStrap12;
import exception.RpcException;
import lombok.extern.slf4j.Slf4j;
import method.Customer;
import service.ClientCall;

/**
 * @author C0ra1
 * @version 1.0
 * ֮������ֱ��������������� ��ע�������ö�Ӧ�İ汾��  ����Ӧ�Ĳ�����װ��֮��Ĳ����м���
 */

@Slf4j
@RpcClientBootStrap(version = "1.2")
public class NIOClientBootStrap {
    public static Customer start() {
        //��ȡ��ǰ��ע���ϵİ汾Ȼ��ȥ������Ӧ��Զ�˷���  ����ķ���
        //��ǰ�ͻ���������class����
        RpcClientBootStrap annotation = ClientCall.class.getAnnotation(RpcClientBootStrap.class);
        String currentVersion = annotation.version();
        //����ע���õİ汾�����ж����ĸ��汾 Ȼ���������
        switch (currentVersion) {
            //1.2�汾֮ǰ���Ǽ������� ���Բ��Ǹ��ݴ�����������е��õ�  ��ʱע�͵�
            // case "1.0":
            //     NIOConsumerBootStrap10.main(null);
            //     break;
            // case "1.1":
            //     NIOConsumerBootStrap11.main(null);
            //     break;
            case "1.2":
                return NIOConsumerBootStrap12.main(null);
//            case "1.4":
//                return NIOConsumerBootStrap14.main(null);
//            case "1.5":
//                return NIOConsumerBootStrap15.main(null);
            default:
                try {
                    throw new RpcException("̫�ż����ֵܣ�����汾��û���أ�Ҫ����������PR");
                } catch (RpcException e) {
                    log.error(e.getMessage(), e);
                    return null;
                }
        }
    }
}
