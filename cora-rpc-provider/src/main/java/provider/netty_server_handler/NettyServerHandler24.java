package provider.netty_server_handler;

import annotation.CompressFunction;
import compress.CompressTypeTool;
import configuration.GlobalConfiguration;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import serialization.SerializationTool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author C0ra1
 * @version 1.0
 */
@Slf4j
public class NettyServerHandler24 extends ChannelInboundHandlerAdapter {
    private final String methodName;

    //Ҫ�����Ӧ�ķ����� ����֪�� netty��������ִ��ʲô����
    public NettyServerHandler24(String methodName) {
        this.methodName = methodName;
    }

    //��̬ʵ�����л�������
    static SerializationTool serializationTool = new SerializationTool();

    //����Ƿ���н�ѹ��
    static boolean openFunction = GlobalConfiguration.class.getAnnotation(CompressFunction.class).isOpenFunction();

    //��ѹ������
    static CompressTypeTool compressTool = new CompressTypeTool();

    //ʵ�ֶ�Ӧ�ķ���

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.info("�յ�����" + ctx.channel().remoteAddress() + "����Ϣ");
        //ʹ�÷���ķ�����ȡ��Ӧ���� ͨ�������ٽ���ִ��
        Class<?> calledClass = null;
        try {
            calledClass = Class.forName("provider.api." + methodName + "ServiceImpl");
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
        }
        assert calledClass != null;
        Method[] methods = calledClass.getMethods();
        Method method = methods[0];

        byte[] msgByteArray = (byte[]) msg;

        //�������Ƚ��н�ѹ  �ٽ��з����л�
        if (openFunction) msgByteArray = compressTool.deCompress((byte[]) msg);

        //�������Ĳ������� ��ȥ���з����л�
        msg = serializationTool.deserialize(msgByteArray, method.getParameterTypes()[0]);

        //��Ϊ�ҽ�����д�� �ڲ����ж��ʵ�ַ���  ���ԾͰ��ն�Ӧ�Ĵ������ ���ж����ĸ�����  ��Ϊû����protoc������ ���Կ϶��ǵ�һ��
        // for (int i = 0; i < methods.length; i++) {
        //     if (methods[i].getParameterTypes()[0]==msg.getClass())
        //     {
        //         method = methods[i];
        //         break;
        //     }
        // }


        Object response = null;
        try {
            Object instance = calledClass.newInstance();
            response = method.invoke(instance, msg);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.error(e.getMessage(), e);
        }
        //��ö�Ӧ��Ϣ�����лش�

        //�ж��Ƿ���Ҫͨ����Ӧ�ķ����������л�  ���л���������
        response = serializationTool.serialize(response);

        //��һ������ѹ��  ѹ�������ļ���
        byte[] responseByteArray = (byte[]) response;
        log.info("before compress" + responseByteArray.length);
        if (openFunction) responseByteArray = compressTool.compress(responseByteArray);
        log.info("after compress" + responseByteArray.length);

        ctx.writeAndFlush(responseByteArray);
    }


    //�����쳣�Ļ� ��ν��д���
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.channel().close();
        cause.printStackTrace();
    }
}

