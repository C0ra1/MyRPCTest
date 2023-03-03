package consumer.netty_client_handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * @author C0ra1
 * @version 1.0
 */
@Slf4j
public class NettyClientHandler21 extends ChannelInboundHandlerAdapter implements Callable {
    //����Ĳ���
    private Object param;
    private Object response;
    private ChannelHandlerContext context;

    public void setParam(Object param) {
        this.param = param;
    }

    //���ɹ����� �͸�ֵ�����Ķ���
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        context = ctx;
        log.info("U?��?*U �ɹ�����");
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) {
        response = msg;
        notifyAll();
    }

    //���õ�ʱ��  �ͽ��д���
    @Override
    public synchronized Object call() {
        context.writeAndFlush(param);
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