package provider.netty;

import codec.AddCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import provider.netty_server_handler.NettyServerHandler22;
import provider.utils.MethodRegister;

import java.lang.reflect.Method;

/**
 * @author C0ra1
 * @version 1.0
 */
@Slf4j
public class NettyServer22 {
    public static void start(String methodName, int port) {
        //������ʵ���߼� ����װ������ķ���������
        start0(methodName, port);
    }

    private static void start0(String methodName, int port) {
        //�Ƚ���ַ����ע��
        MethodRegister.register(methodName, "127.0.0.1", port);

        //��ʼ������Ӧ��netty�����
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        //������Ӧ�Ĺ����� ���ڴ���ͬ���¼�
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            //���г�ʼ��
            serverBootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class) //����ʵ�ֵ�ͨ��
                    .option(ChannelOption.SO_BACKLOG, 128) //�����̶߳��еõ������Ӹ���
                    .childOption(ChannelOption.SO_KEEPALIVE, true) //���ñ��ֻ����״̬
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            log.info(socketChannel.remoteAddress() + "��������");
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //��ӵĴ����� ������Ӧ��ע�����
                            Method method = null;
                            try {
                                method = Class.forName("provider.api." + methodName + "ServiceImpl").getMethods()[0];
                            } catch (ClassNotFoundException e) {
                                log.error(e.getMessage(), e);
                            }
                            assert method != null;
                            AddCodec.addCodec(pipeline, method, false);
                            //�����ֱ���Ƿ��������� �����Ƿ�������
                            pipeline.addLast(new NettyServerHandler22(methodName));

                        }
                    });

            ChannelFuture channelFuture = null;
            try {
                channelFuture = serverBootstrap.bind(port).sync();
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }

            //�����ӽ�����м���
            assert channelFuture != null;
            channelFuture.addListener((ChannelFutureListener) channelFuture1 -> {
                if (channelFuture1.isSuccess()) log.info("������" + port + "�˿�");
                else log.info("���Ӷ˿�ʧ��");
            });

            //�ȴ��ر� ͬ��
            try {
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        } finally {
            //�����˵Ļ�  �ͽ��йر���
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
