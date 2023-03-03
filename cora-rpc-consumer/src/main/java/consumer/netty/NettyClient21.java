package consumer.netty;

import consumer.netty_client_handler.NettyClientHandler21;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author C0ra1
 * @version 1.0
 */
@Slf4j
public class NettyClient21 {

    //�̳߳� ʵ���첽����
    private static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    static NettyClientHandler21 clientHandler;

    public static void initClient(String hostName, int port) {

        clientHandler = new NettyClientHandler21();
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
                            pipeline.addLast(new StringEncoder());//�������ӱ������ ��Ȼ����ʶ���ഫ����ȥ
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(clientHandler);
                        }
                    });

            //��������
            bootstrap.connect(hostName, port).sync();

        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static Object callMethod(String hostName, int port, Object param) {

        //�����ж���ط����е��õ� ����ֻ����һ��
        initClient(hostName, port);
        clientHandler.setParam(param);
        //����������й�ϵ������ ֱ�ӵ���
        try {
            return executor.submit(clientHandler).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
