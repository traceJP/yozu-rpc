package com.tracejp.yozu.rpc.core.transport;


import com.tracejp.yozu.rpc.core.provider.ServiceProviderManager;
import com.tracejp.yozu.rpc.core.transport.codec.RpcMessageDecoder;
import com.tracejp.yozu.rpc.core.transport.codec.RpcMessageEncoder;
import com.tracejp.yozu.rpc.core.transport.handler.NettyRpcServerHandler;
import com.tracejp.yozu.rpc.core.transport.hook.RpcShutdownHook;
import com.tracejp.yozu.rpc.core.utils.RuntimeUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * <p> Netty 服务端 <p/>
 *
 * @author traceJP
 * @since 2023/10/31 9:31
 */
public class NettyRpcServer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(NettyRpcServer.class);

    public final InetSocketAddress address;

    private final RpcShutdownHook rpcShutdownHook;

    private final RpcMessageEncoder rpcMessageEncoder;

    private final NettyRpcServerHandler nettyRpcServerHandler;

    public NettyRpcServer(RpcShutdownHook rpcShutdownHook,
                          RpcMessageEncoder rpcMessageEncoder,
                          ServiceProviderManager serviceProviderManager,
                          InetSocketAddress address) {
        this.rpcShutdownHook = rpcShutdownHook;
        this.rpcMessageEncoder = rpcMessageEncoder;
        nettyRpcServerHandler = new NettyRpcServerHandler(serviceProviderManager);
        this.address = address;
    }

    @Override
    public void run() {
        rpcShutdownHook.clearAll();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        DefaultEventExecutorGroup serverHandlerGroup = new DefaultEventExecutorGroup(
                RuntimeUtil.cpus() * 2
        );
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            p.addLast(rpcMessageEncoder);
                            p.addLast(new RpcMessageDecoder());
                            p.addLast(serverHandlerGroup, nettyRpcServerHandler);
                        }
                    });
            ChannelFuture f = b.bind(address).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("occur exception when start server:", e);
        } finally {
            logger.error("shutdown bossGroup and workerGroup");
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            serverHandlerGroup.shutdownGracefully();
        }
    }

}
