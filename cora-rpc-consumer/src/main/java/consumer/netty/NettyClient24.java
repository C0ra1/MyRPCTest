package consumer.netty;

import annotation.HeartBeatTool;
import codec.AddCodec;
import configuration.GlobalConfiguration;
import consumer.netty_client_handler.NettyClientHandler21;
import consumer.netty_client_handler.NettyClientHandler24;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author C0ra1
 * @version 1.0
 */
@Slf4j
public class NettyClient24 {

    //�̳߳� ʵ���첽����
    private static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static final HeartBeatTool heartBeatToolAnnotation = GlobalConfiguration.class.getAnnotation(HeartBeatTool.class);
    // static NettyClientHandler24 clientHandler;//����û��ϵ ��Ϊÿ�ζ��½�һ��
    private static final ThreadLocal<NettyClientHandler24> nettyClientHandlerThreadLocal = ThreadLocal.withInitial(() -> new NettyClientHandler24());

    public static Object callMethod(String hostName, int port, Object param, Method method) {

        NettyClientHandler24 clientHandler = nettyClientHandlerThreadLocal.get();
        //�����ͻ��˼���
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            bootstrap.group(workGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();

                            //�ӱ���������߼������ݶ�Ӧ��ע����б���������� ��������ʵ�ֶ�Ӧ���߼� //
                            AddCodec.addCodec(pipeline, method, true);

                            /*
                                v2.5������Ӷ�д���д�����
                                IdleStateHandler��netty�ṩ�Ĵ����д���еĴ�����
                                readerIdleTimeSeconds  �೤ʱ��û�ж� �ͻᴫ��һ�����������м��
                                writerIdleTimeSeconds  �೤ʱ��û��д �ͻᴫ��һ�����������м��
                                allIdleTimeSeconds     �೤ʱ��û�ж�д �ͻᴫ��һ�����������м��
                                ���¼�������ᴫ�ݸ���һ�����������д���ֻ��Ҫ����һ����������ʵ��userEventTriggered�¼�����
                             */
                            //ʱ���ʵ��ʵ�� ����ע�� �ж��Ƿ���
                            //��ס����һ����Ҫ��һ�������� �����������¼�
                            if (heartBeatToolAnnotation.isOpenFunction()) {
                                pipeline.addLast(new IdleStateHandler(
                                        heartBeatToolAnnotation.readerIdleTimeSeconds(),
                                        heartBeatToolAnnotation.writerIdleTimeSeconds(),
                                        heartBeatToolAnnotation.allIdleTimeSeconds())
                                );
                            }
                            pipeline.addLast(clientHandler);
                        }
                    });

            //��������
            bootstrap.connect(hostName, port).sync();

        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        //�����ж���ط����е��õ� ����ֻ����һ��
        // initClient(hostName,port,method);
        clientHandler.setParam(param);
        clientHandler.setMethod(method);
        //����������й�ϵ������ ֱ�ӵ���
        Object response = null;
        try {
            response = executor.submit(clientHandler).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage(), e);
        }
        nettyClientHandlerThreadLocal.remove(); //һ��handler���ܼӵ������ܵ��� ��˵�ǰ�
        return response;
    }
}
