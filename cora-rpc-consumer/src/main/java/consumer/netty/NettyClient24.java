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

    //线程池 实现异步调用
    private static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static final HeartBeatTool heartBeatToolAnnotation = GlobalConfiguration.class.getAnnotation(HeartBeatTool.class);
    // static NettyClientHandler24 clientHandler;//跟他没关系 因为每次都新建一个
    private static final ThreadLocal<NettyClientHandler24> nettyClientHandlerThreadLocal = ThreadLocal.withInitial(() -> new NettyClientHandler24());

    public static Object callMethod(String hostName, int port, Object param, Method method) {

        NettyClientHandler24 clientHandler = nettyClientHandlerThreadLocal.get();
        //建立客户端监听
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            bootstrap.group(workGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();

                            //加编解码器的逻辑，根据对应的注解进行编码器的添加 这里面有实现对应的逻辑 //
                            AddCodec.addCodec(pipeline, method, true);

                            /*
                                v2.5更新添加读写空闲处理器
                                IdleStateHandler是netty提供的处理读写空闲的处理器
                                readerIdleTimeSeconds  多长时间没有读 就会传递一个心跳包进行检测
                                writerIdleTimeSeconds  多长时间没有写 就会传递一个心跳包进行检测
                                allIdleTimeSeconds     多长时间没有读写 就会传递一个心跳包进行检测
                                当事件触发后会传递给下一个处理器进行处理，只需要在下一个处理器中实现userEventTriggered事件即可
                             */
                            //时间和实不实现 根据注解 判断是否开启
                            //记住后面一定是要有一个处理器 用来处理触发事件
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

            //进行连接
            bootstrap.connect(hostName, port).sync();

        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        //我是有多个地方进行调用的 不能只连接一个
        // initClient(hostName,port,method);
        clientHandler.setParam(param);
        clientHandler.setMethod(method);
        //接下来这就有关系到调用 直接调用
        Object response = null;
        try {
            response = executor.submit(clientHandler).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage(), e);
        }
        nettyClientHandlerThreadLocal.remove(); //一个handler不能加到两个管道中 你说是吧
        return response;
    }
}
