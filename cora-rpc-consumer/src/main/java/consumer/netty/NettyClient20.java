package consumer.netty;

import consumer.netty_client_handler.NettyClientHandler20;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author C0ra1
 * @version 1.0
 */
@Slf4j
public class NettyClient20 {
    public static void start(String hostName, int port) {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            bootstrap.group(workGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new NettyClientHandler20());
                        }
                    });

            ChannelFuture channelFuture = null;
            try {
                channelFuture = bootstrap.connect(hostName, port).sync();
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }

            //��Ϊ������ʵ�Ѿ���ͬ��  ��������ļ��������Բ���
            assert channelFuture != null;
            channelFuture.addListener((ChannelFutureListener) channelFuture1 -> {
                if (channelFuture1.isSuccess()) {
                    log.info("����" + hostName + ":" + port + "�ɹ�");
                } else {
                    log.info("����" + hostName + ":" + port + "ʧ��");
                }
            });

            //�����ر��¼����������첽�ģ�����ת��Ϊͬ���¼�
            try {
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        } finally {
            //���ŵĹر� group
            workGroup.shutdownGracefully();
        }
    }
}
