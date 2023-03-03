package service.call;

import annotation.RpcToolsSelector;
import exception.RpcException;
import lombok.extern.slf4j.Slf4j;
import service.ServerCall;
import service.call.netty_call.NettyServerCall;
import service.call.nio_call.NIOServerCall;

import java.io.IOException;

/**
 * @author C0ra1
 * @version 1.0
 */
@Slf4j
public class ChosenServerCall {
    public static void start() throws IOException {
        RpcToolsSelector annotation = ServerCall.class.getAnnotation(RpcToolsSelector.class);
        switch (annotation.rpcTool()) {
            case "Netty":
                NettyServerCall.main(null);
                break;
            case "Nio":
                NIOServerCall.main(null);
                break;
            default:
                try {
                    throw new RpcException("��ʱ��û�и÷�������������Ŭ��������"); //�׳��쳣����в���
                } catch (RpcException e) {
                    log.error(e.getMessage(), e);
                }
        }
    }
}
