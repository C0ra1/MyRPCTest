package consumer.netty_client_handler;

import annotation.CompressFunction;
import compress.CompressTypeTool;
import configuration.GlobalConfiguration;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import serialization.SerializationTool;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * @author C0ra1
 * @version 1.0
 */
@Slf4j
public class NettyClientHandler24 extends ChannelInboundHandlerAdapter implements Callable {
    //����Ĳ���
    private Object param;
    private Method method;
    private Object response;
    private ChannelHandlerContext context;

    //���л�����
    static SerializationTool serializationTool = new SerializationTool();

    //�ж��Ƿ���н�ѹ��
    static boolean openFunction = GlobalConfiguration.class.getAnnotation(CompressFunction.class).isOpenFunction();

    //��ѹ������
    static CompressTypeTool compressTool = new CompressTypeTool();

    public void setParam(Object param) {
        this.param = param;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    //���ɹ����� �͸�ֵ�����Ķ���
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        context = ctx;
        log.info("U?��?*U �ɹ�����");
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) {

        byte[] msgByteArray = (byte[]) msg;
        // �����Ƿ��ѹ ���Ȱ��յ�����Ϣ���н�ѹ�ٽ��з����л�
        if (openFunction) msgByteArray = compressTool.deCompress((byte[]) msg);

        //����Ҫ���н��� ��ô���������Ϣ  ��������������msg �Ǿʹ��������Ŀ϶��Ǹ�byte[]
        // ��������Ҫ�ķ������н��� ��������Ӧ���Ƿ�����response��
        //������Ҫ�ķ������ͽ��з����л�
        msg = serializationTool.deserialize(msgByteArray, method.getReturnType());

        response = msg;
        notifyAll();
    }

    //���õ�ʱ��  �ͽ��д���
    @Override
    public synchronized Object call() {
        //���������Ŀ�ľ��Ǳ���ԭ����paramʵ�ʲ������ͣ������ص�ʱ�� ���Ե��������л���ģ��
        Object request = param;
        //�ж��Ƿ���Ҫ�������л� ��Ϊʹ������������л� ����û����Ӧ�Ľ����� 2.4֮�� ������stringҲ�������л�
        request = serializationTool.serialize(request);

        //��һ������ѹ��
        byte[] requestByteArray = (byte[]) request;

        //�ж��Ƿ����ѹ��
        if (openFunction) requestByteArray = compressTool.compress(requestByteArray);

        context.writeAndFlush(requestByteArray);

        try {
            wait();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        return response;
    }

    //�����ж��Ƿ�����˿����¼�  ����������Ǿͽ�����Ӧ�Ĵ���
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            //����һ���¼��ֶ�
            String eventType = null;
            switch (event.state()) {
                case READER_IDLE:
                    eventType = "������";
                    break;
                case WRITER_IDLE:
                    eventType = "д����";
                    break;
                case ALL_IDLE:
                    eventType = "��д����";
                    break;
            }
            log.info(ctx.channel().remoteAddress() + "������ʱ�¼�" + eventType + "�����ӹر�");
            ctx.close();
        }
    }

    //�쳣����
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage(), cause);
        ctx.close();
    }
}

