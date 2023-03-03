package consumer.netty_client_handler;

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
public class NettyClientHandler20 extends ChannelInboundHandlerAdapter {

    //ͨ�������ͻᷢ����Ϣ
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.copiedBuffer("��ã������", CharsetUtil.UTF_8));
    }

    //������յ���Ϣ
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        log.info("�յ�����" + ctx.channel().remoteAddress() + "����Ϣ" + buf.toString(CharsetUtil.UTF_8));
    }
}

