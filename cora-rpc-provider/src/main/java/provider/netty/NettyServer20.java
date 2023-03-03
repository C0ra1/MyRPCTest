package provider.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import provider.netty_server_handler.NettyServerHandler20;

/**
 * @author C0ra1
 * @version 1.0
 */
@Slf4j
public class NettyServer20 {
    public static void start(String hostName, int port) {
        //�÷������NettyServer�ĳ�ʼ��  �ú����뿴 ����ô������
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            serverBootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class) //������ʵ�ֵ�ͨ��
                    .option(ChannelOption.SO_BACKLOG, 128) //�����̶߳��еõ������Ӹ���
                    .childOption(ChannelOption.SO_KEEPALIVE, true) //���ñ��ֻ����״̬
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            //ÿ���û������϶�����г�ʼ��
                            log.info("�ͻ�socketChannel hashcode=" + socketChannel.hashCode());
                            ChannelPipeline pipeline = socketChannel.pipeline();//ÿ��ͨ������Ӧһ���ܵ� �����������ܵ����
                            pipeline.addLast(new NettyServerHandler20());
                        }
                    });

            log.info("������ is ready");

            //���� ͬ��
            ChannelFuture cf = null;
            try {
                cf = serverBootstrap.bind(hostName, port).sync();
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }

            assert cf != null;
            cf.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("�����˿�" + port + "�ɹ�");
                } else {
                    log.info("�����˿�" + port + "ʧ��");
                }
            });

            //�Թر�ͨ�����м���
            try {
                cf.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        } finally {
            //���ŵĹر�������Ⱥ
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
