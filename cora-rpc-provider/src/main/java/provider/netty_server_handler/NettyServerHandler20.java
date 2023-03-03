package provider.netty_server_handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author C0ra1
 * @version 1.0
 */
@Slf4j
public class NettyServerHandler20 extends ChannelInboundHandlerAdapter {

    //��ȡ����
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //����Ϣ���ж�ȡ ֱ�������Ϳ�����
        ByteBuf buf = (ByteBuf) msg;
        log.info("�ͻ��˷�����Ϣ�ǣ�" + buf.toString(CharsetUtil.UTF_8));
        log.info("�ͻ��˵�ַ��" + ctx.channel().remoteAddress());
    }

    //���ݶ�ȡ���  �����Ѿ����õ���byteBuffer
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        //��ȡ��Ͻ��л���  д�ز�ˢ��
        ctx.writeAndFlush(Unpooled.copiedBuffer("success", CharsetUtil.UTF_8));
    }

    //�����쳣
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        //�쳣���� �����Ƚ�ͨ���������Ĺر�  ÿ��ctx��Ӧ�ľ���handler����
        ctx.close();
        cause.printStackTrace();
    }
}
