package consumer.netty_client_handler;

import annotation.RpcSerializationSelector;
import configuration.GlobalConfiguration;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import serialization.hessian.HessianUtils;
import serialization.kryo.KryoUtils;
import serialization.protostuff.ProtostuffUtils;

import java.util.concurrent.Callable;

/**
 * @author C0ra1
 * @version 1.0
 */
@Slf4j
public class NettyClientHandler22 extends ChannelInboundHandlerAdapter implements Callable {
    //����Ĳ���
    private Object param;
    private Object response;
    private ChannelHandlerContext context;
    private boolean isProtostuff;
    private boolean isKryo;
    private boolean isHessian;

    public void setParam(Object param) {
        this.param = param;
    }

    //���ɹ����� �͸�ֵ�����Ķ���
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        context = ctx;
        log.info("U?��?*U �ɹ�����");
        String serialization = GlobalConfiguration.class.getAnnotation(RpcSerializationSelector.class).RpcSerialization();
        if (serialization.equals("protostuff")) isProtostuff = true;
        else if (serialization.equals("kryo")) isKryo = true;
        else if (serialization.equals("hessian")) isHessian = true;
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) {
        //����Ҫ���н��� ��ô���������Ϣ  ��������������msg �Ǿʹ��������Ŀ϶��Ǹ�byte[] ��������Ҫ�ķ������н���
        if (isProtostuff && msg.getClass() != String.class) {
            ProtostuffUtils protostuffUtils = new ProtostuffUtils();
            //�����л���ģ�� �Ǹ����Ҵ������Ĳ������иı��
            msg = protostuffUtils.deserialize((byte[]) msg, param.getClass());
        }
        if (isKryo && msg.getClass() != String.class) {
            KryoUtils kryoUtils = new KryoUtils();
            //�����л���ģ�� �Ǹ����Ҵ������Ĳ������иı��
            msg = kryoUtils.deserialize((byte[]) msg, param.getClass());
        }
        if (isHessian && msg.getClass() != String.class) {
            HessianUtils hessianUtils = new HessianUtils();
            //�����л���ģ�� �Ǹ����Ҵ������Ĳ������иı��
            msg = hessianUtils.deserialize((byte[]) msg, param.getClass());
        }
        response = msg;
        notifyAll();
    }

    //���õ�ʱ��  �ͽ��д���
    @Override
    public synchronized Object call() {

        //���������Ŀ�ľ��Ǳ���ԭ����paramʵ�ʲ������ͣ������ص�ʱ�� ���Ե��������л���ģ��
        Object request = param;
        //�ж��Ƿ���Ҫprotostuff�������л� ��Ϊʹ������������л� ����û����Ӧ�Ľ�����
        if (isProtostuff && param.getClass() != String.class) {
            ProtostuffUtils protostuffUtils = new ProtostuffUtils();
            request = protostuffUtils.serialize(param);
        }
        if (isKryo && param.getClass() != String.class) {
            KryoUtils kryoUtils = new KryoUtils();
            request = kryoUtils.serialize(param);
        }
        if (isHessian && param.getClass() != String.class) {
            HessianUtils hessianUtils = new HessianUtils();
            request = hessianUtils.serialize(param);
        }

        context.writeAndFlush(request);
        try {
            wait();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        return response;
    }

    //�쳣����
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}

