package service.call;

import annotation.RpcToolsSelector;
import exception.RpcException;
import lombok.extern.slf4j.Slf4j;
import method.Customer;
import service.ClientCall;
import service.call.netty_call.NettyClientCall;
import service.call.nio_call.NIOClientCall;

/**
 * @author C0ra1
 * @version 1.0
 */
@Slf4j
public class ChosenClientCall {
    public static Customer start() {
        RpcToolsSelector annotation = ClientCall.class.getAnnotation(RpcToolsSelector.class);
        switch (annotation.rpcTool()) {
            //��ʱ��û�� return�Ķ���
            case "Netty":
                return NettyClientCall.main(null);
            case "Nio":
                return NIOClientCall.main(null);
            default:
                try {
                    throw new RpcException("��ʱ��û�и÷�������������Ŭ��������"); //�׳��쳣����в���
                } catch (RpcException e) {
                    log.error(e.getMessage(), e);
                    return null;
                }
        }
    }
}