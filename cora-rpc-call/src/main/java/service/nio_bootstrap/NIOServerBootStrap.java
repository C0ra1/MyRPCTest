package service.nio_bootstrap;

import annotation.RpcClientBootStrap;
import annotation.RpcMethodCluster;
import annotation.RpcServerBootStrap;
import exception.RpcException;
import init.ZK;
import lombok.extern.slf4j.Slf4j;
import provider.bootstrap.nio.NIOProviderBootStrap10;
import provider.bootstrap.nio.NIOProviderBootStrap11;
import provider.bootstrap.nio.NIOProviderBootStrap12;
import provider.bootstrap.nio.NIOProviderBootStrap14;
import service.ServerCall;

import java.io.IOException;

/**
 * @author C0ra1
 * @version 1.0
 * ֮������ֱ��������������� ��ע�������ö�Ӧ�İ汾��
 * ����Ӧ�Ĳ�����װ��֮��Ĳ����м���
 */

@Slf4j
@RpcClientBootStrap(version = "1.2")
public class NIOServerBootStrap {

    public static void start() throws IOException {

        //�ȶ�ZK���г�ʼ��
        ZK.init();
        RpcServerBootStrap annotation = ServerCall.class.getAnnotation(RpcServerBootStrap.class);
        //��ǰ����������� class����
        String currentServerBootStrapVersion = annotation.version();

        //��ȡ��Ӧ�ķ����͸��� Ȼ���������
        //1.��ȡ��Ӧ���� �ڻ�ȡ��Ӧ��ע��  ע���е�����
        RpcMethodCluster nowAnnotation = ServerCall.class.getAnnotation(RpcMethodCluster.class);
        String[] methods = nowAnnotation.method();
        int[] startNums = nowAnnotation.startNum();
        //����������Ǿͷ���
        //����������Ǿͷ���  ���� ��һ�� ���׳��쳣
        try {
            if (methods.length == 0) throw new RpcException("���뷽����Ϊ0");
            if (methods.length != startNums.length) throw new RpcException("���뷽���������޷�һһ��Ӧ");
        } catch (RpcException e) {
            log.error(e.getMessage(), e);
            return;
        }

        //2.��Ҫ�����һ�𴫹�ȥ  �������Ϸֱ� ���¾��Ƕ˿ںŻ��������
        StringBuilder methodBuilder = new StringBuilder();
        StringBuilder numBuilder = new StringBuilder();

        //��Ϊ��������һ�� �ǾͲ���������ѭ����
        for (int i = 0; i < methods.length; ++i) {
            methodBuilder.append(methods[i]);
            methodBuilder.append(",");
            numBuilder.append(startNums[i]);
            numBuilder.append(",");
        }
        //��ȥ���������,
        methodBuilder.deleteCharAt(methodBuilder.length() - 1);
        numBuilder.deleteCharAt(numBuilder.length() - 1);

        switch (currentServerBootStrapVersion) {
            case "1.0":
                NIOProviderBootStrap10.main(null);
                break;
            case "1.1":
                NIOProviderBootStrap11.main(null);
                break;
            case "1.2":
                NIOProviderBootStrap12.main(null);
                break;
            case "1.4":
                //1.4 ������ע������ zk
                NIOProviderBootStrap14.main(new String[]{methodBuilder.toString(), numBuilder.toString()});
                break;
//            case "1.5":
//                //1.5 ��ע�����Ļ�����nacos
//                NIOProviderBootStrap15.main(new String[]{methodBuilder.toString(), numBuilder.toString()});
//                break;
            default:
                try {
                    throw new RpcException("̫�ż����ֵܣ�����汾��û���أ�Ҫ����������PR");
                } catch (RpcException e) {
                    log.error(e.getMessage(), e);
                }
        }
    }
}

