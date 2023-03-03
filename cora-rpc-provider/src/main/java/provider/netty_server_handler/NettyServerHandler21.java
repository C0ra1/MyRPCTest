package provider.netty_server_handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author C0ra1
 * @version 1.0
 */
@Slf4j
public class NettyServerHandler21 extends ChannelInboundHandlerAdapter {
    private final String methodName;

    //Ҫ�����Ӧ�ķ����� ����֪�� netty��������ִ��ʲô����
    public NettyServerHandler21(String methodName) {
        this.methodName = methodName;
    }

    //ʵ�ֶ�Ӧ�ķ���

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.info("�յ�����" + ctx.channel().remoteAddress() + "����Ϣ");
        //ʹ�÷���ķ�����ȡ��Ӧ���� ͨ�������ٽ���ִ��
        Object response = null;
        try {
            Class<?> calledClass = Class.forName("provider.api." + methodName + "ServiceImpl");
            Method method = calledClass.getMethod("say" + methodName, String.class);
            Object instance = calledClass.newInstance();
            response = method.invoke(instance, msg.toString());
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.error(e.getMessage(), e);
        }

        //��ö�Ӧ��Ϣ�����лش�
        ctx.writeAndFlush(response);
    }

    //�����쳣�Ļ� ��ν��д���
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.channel().close();
        log.error(cause.getMessage(), cause);
    }
}
