package service.call.nio_call;

import service.nio_bootstrap.NIOServerBootStrap;

import java.io.IOException;

/**
 * @author C0ra1
 * @version 1.0
 * ͨ�������࣬�������߼�����ServerBootStrap��
 */
public class NIOServerCall {
    public static void main(String[] args) throws IOException {
        NIOServerBootStrap.start();
    }
}
