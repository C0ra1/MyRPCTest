package provider.netty_server_handler;

import annotation.RpcSerializationSelector;
import configuration.GlobalConfiguration;
import exception.RpcException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import serialization.hessian.HessianUtils;
import serialization.kryo.KryoUtils;
import serialization.protostuff.ProtostuffUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author C0ra1
 * @version 1.0
 */
@Slf4j
public class NettyServerHandler22 extends ChannelInboundHandlerAdapter {
    private final String methodName;
    private boolean isProtostuff;
    private boolean isKryo;
    private boolean isHessian;

    //Ҫ�����Ӧ�ķ����� ����֪�� netty��������ִ��ʲô����
    public NettyServerHandler22(String methodName) {
        this.methodName = methodName;
    }

    //ʵ�ֶ�Ӧ�ķ���


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        String serialization = GlobalConfiguration.class.getAnnotation(RpcSerializationSelector.class).RpcSerialization();
        switch (serialization) {
            case "protostuff":
                isProtostuff = true;
                break;
            case "kryo":
                isKryo = true;
                break;
            case "hessian":
                isHessian = true;
                break;
            default:
                try {
                    throw new RpcException("û�ж�Ӧ�����л���");
                } catch (RpcException e) {
                    log.error(e.getMessage(), e);
                }
        }
    }

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

        //����Ҵ������protostuff ������������Ӧ��byte���� ���Ҿ��Ҳ�����Ӧ�ķ��� ����Ĭ���ǵ�һ������  ���о����жϲ����ַ���
        if (isProtostuff && msg.getClass() != String.class) {
            ProtostuffUtils protostuffUtils = new ProtostuffUtils();
            msg = protostuffUtils.deserialize((byte[]) msg, method.getParameterTypes()[0]);
        } else if (isKryo && msg.getClass() != String.class) {
            KryoUtils kryoUtils = new KryoUtils();
            msg = kryoUtils.deserialize((byte[]) msg, method.getParameterTypes()[0]);
        } else if (isHessian && msg.getClass() != String.class) {
            HessianUtils hessianUtils = new HessianUtils();
            msg = hessianUtils.deserialize((byte[]) msg, method.getParameterTypes()[0]);
        } else
        //��Ϊ�ҽ�����д�� �ڲ����ж��ʵ�ַ���  ���ԾͰ��ն�Ӧ�Ĵ������ ���ж����ĸ�����
        {
            for (int i = 0; i < methods.length; ++i) {
                if (methods[i].getParameterTypes()[0] == msg.getClass()) {
                    method = methods[i];
                    break;
                }
            }
        }

        Object response = null;
        try {
            Object instance = calledClass.newInstance();
            response = method.invoke(instance, msg);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.error(e.getMessage(), e);
        }
        //��ö�Ӧ��Ϣ�����лش�

        //�ж��Ƿ���Ҫͨ����Ӧ�ķ����������л�
        assert response != null;
        if (isProtostuff && response.getClass() != String.class) {
            ProtostuffUtils protostuffUtils = new ProtostuffUtils();
            response = protostuffUtils.serialize(response);
        }
        if (isKryo && response.getClass() != String.class) {
            KryoUtils kryoUtils = new KryoUtils();
            response = kryoUtils.serialize(response);
        }
        if (isHessian && response.getClass() != String.class) {
            HessianUtils hessianUtils = new HessianUtils();
            response = hessianUtils.serialize(response);
        }

        ctx.writeAndFlush(response);
    }

    //�����쳣�Ļ� ��ν��д���
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.channel().close();
        log.error(cause.getMessage(), cause);
    }
}

