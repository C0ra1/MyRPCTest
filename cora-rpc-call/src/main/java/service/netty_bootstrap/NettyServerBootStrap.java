package service.netty_bootstrap;

import annotation.RpcMethodCluster;
import annotation.RpcServerBootStrap;
import exception.RpcException;
import init.ZK;
import lombok.extern.slf4j.Slf4j;
import provider.bootstrap.netty.NettyProviderBootStrap20;
import provider.bootstrap.netty.NettyProviderBootStrap21;
import provider.bootstrap.netty.NettyProviderBootStrap22;
import provider.bootstrap.netty.NettyProviderBootStrap24;
import service.ServerCall;

/**
 * @author C0ra1
 * @version 1.0
 */
@Slf4j
public class NettyServerBootStrap {
    public static void start() {
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
        for (int i = 0; i < methods.length; ++i) {
            methodBuilder.append(methods[i]);
            methodBuilder.append(",");
            numBuilder.append(startNums[i]);
            numBuilder.append(",");
        }
        methodBuilder.deleteCharAt(methodBuilder.length() - 1);
        numBuilder.deleteCharAt(numBuilder.length() - 1);

        //���ݶ�Ӧ�������汾��������
        switch (currentServerBootStrapVersion) {

            case "2.0": //2.0�汾ֻ�ǽ����˲��� �򵥵�ʵ����Զ����Ϣ����
                NettyProviderBootStrap20.main(new String[]{"127.0.0.1", String.valueOf(6668)});
                break;
            case "2.1":
                NettyProviderBootStrap21.main(new String[]{methodBuilder.toString(), numBuilder.toString()});
                break;
            case "2.2": //���� ���� ��������  ����汾ʱ�������л��Ĳ���
                NettyProviderBootStrap22.main(new String[]{methodBuilder.toString(), numBuilder.toString()});
                break;
            case "2.4": //����汾�Ǹ���汾 �������л����߳��ֺ�ʹ��
//            case "2.5":
//            case "2.6":
//            case "2.7":
//            case "2.8":
//            case "2.9":
//            case "2.10":
//            case "2.11":
                NettyProviderBootStrap24.main(new String[]{methodBuilder.toString(), numBuilder.toString()});
                break;
            default:
                try {
                    throw new RpcException("�ð汾��û���أ���������뷨����˽���ң��������pr");
                } catch (RpcException e) {
                    log.error(e.getMessage(), e);
                }
        }
    }
}

