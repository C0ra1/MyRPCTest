package service;

import annotation.RpcMethodCluster;
import annotation.RpcServerBootStrap;
import annotation.RpcToolsSelector;
import service.call.ChosenServerCall;

import java.io.IOException;

/**
 * @author C0ra1
 * @version 1.0
 * �ܷ���������� �û�����  ע���� ע��ʲô������ȥ
 * ���õ���ʲô�汾�ķ������������
 * ������ע��������Ͷ�Ӧ�ķ���һһ��Ӧ
 */
@RpcMethodCluster(method = {"Hello", "Bye", "GetName", "GetPerson"}, startNum = {2, 3, 3, 1})
@RpcServerBootStrap(version = "1.2")
@RpcToolsSelector(rpcTool = "Nio")
public class ServerCall {
    public static void main(String[] args) throws IOException {
        ChosenServerCall.start();
    }
}
