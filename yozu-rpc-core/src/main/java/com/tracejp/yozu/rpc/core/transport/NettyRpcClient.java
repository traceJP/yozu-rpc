package com.tracejp.yozu.rpc.core.transport;

import com.tracejp.yozu.rpc.core.enums.CompressTypeEnum;
import com.tracejp.yozu.rpc.core.enums.RpcMessageTypeEnum;
import com.tracejp.yozu.rpc.core.enums.SerializationTypeEnum;
import com.tracejp.yozu.rpc.core.provider.IServiceProvider;
import com.tracejp.yozu.rpc.core.spring.properties.RequestConfigurationProperties;
import com.tracejp.yozu.rpc.core.transport.codec.RpcMessageDecoder;
import com.tracejp.yozu.rpc.core.transport.codec.RpcMessageEncoder;
import com.tracejp.yozu.rpc.core.transport.context.ChannelProviderContext;
import com.tracejp.yozu.rpc.core.transport.context.UnprocessedRequestContext;
import com.tracejp.yozu.rpc.core.transport.domain.RpcMessage;
import com.tracejp.yozu.rpc.core.transport.domain.RpcRequest;
import com.tracejp.yozu.rpc.core.transport.domain.RpcResponse;
import com.tracejp.yozu.rpc.core.transport.handler.NettyRpcClientHandler;
import com.tracejp.yozu.rpc.core.utils.NextNumberUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * <p> Netty 客户端 <p/>
 *
 * @author traceJP
 * @since 2023/10/30 16:37
 */
public class NettyRpcClient implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(NettyRpcClient.class);

    private Bootstrap bootstrap;

    private EventLoopGroup eventLoopGroup;

    private EventLoopGroup requestEventLoopGroup;

    /**
     * 请求消息 ID 生成 Key
     */
    private static final String REQUEST_INTEGER_ID_KEY = "RPC_MESSAGE_ENCODER_REQUEST_ID";

    private final IServiceProvider serviceProvider;

    private final RpcMessageEncoder rpcMessageEncoder;

    private final NettyRpcClientHandler nettyRpcClientHandler;

    private final RequestConfigurationProperties requestConfigurationProperties;

    public NettyRpcClient(IServiceProvider serviceProvider,
                          RpcMessageEncoder rpcMessageEncoder,
                          RequestConfigurationProperties requestConfigurationProperties) {
        this.serviceProvider = serviceProvider;
        this.rpcMessageEncoder = rpcMessageEncoder;
        this.requestConfigurationProperties = requestConfigurationProperties;
        nettyRpcClientHandler = new NettyRpcClientHandler(this, requestConfigurationProperties);
    }

    @Override
    public void run() {
        eventLoopGroup = new NioEventLoopGroup();
        requestEventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)  // 连接超时时间
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        p.addLast(rpcMessageEncoder);
                        p.addLast(new RpcMessageDecoder());
                        p.addLast(nettyRpcClientHandler);
                    }
                });
    }

    /**
     * 发送一个请求
     */
    public Promise<RpcResponse<Object>> sendRpcRequest(RpcRequest request) {
        DefaultPromise<RpcResponse<Object>> promise = new DefaultPromise<>(requestEventLoopGroup.next());

        InetSocketAddress address = serviceProvider.lookupService(request);
        Channel channel = getChannel(address);
        if (channel.isActive()) {
            UnprocessedRequestContext.put(request.getRequestId(), promise);

            // 构建请求消息
            RpcMessage message = new RpcMessage();
            message.setMessageType(RpcMessageTypeEnum.REQUEST_TYPE.getType());
            message.setRequestId(NextNumberUtil.getIncr(REQUEST_INTEGER_ID_KEY));
            message.setData(request);
            SerializationTypeEnum serializationTypeEnum = SerializationTypeEnum
                    .nameOf(requestConfigurationProperties.getRpcSerialize());
            CompressTypeEnum compressTypeEnum = CompressTypeEnum
                    .nameOf(requestConfigurationProperties.getRpcCompress());
            message.setCodec(serializationTypeEnum.getCode());
            message.setCompress(compressTypeEnum.getCode());

            // 发送消息 - 如果失败则关闭 channel
            channel.writeAndFlush(message).addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    future.channel().close();
                    promise.setFailure(future.cause());
                    logger.error("Send failed:", future.cause());
                }
            });
        } else {
            logger.error("The channel is not active!");
            throw new IllegalStateException();
        }
        return promise;
    }

    /**
     * 获取服务端的 Channel
     */
    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = ChannelProviderContext.get(inetSocketAddress);
        if (channel == null) {
            channel = doConnect(inetSocketAddress);
            ChannelProviderContext.put(inetSocketAddress, channel);
        }
        return channel;
    }

    /**
     * 连接服务端
     */
    public Channel doConnect(InetSocketAddress inetSocketAddress) {
        try {
            ChannelFuture connect = bootstrap.connect(inetSocketAddress);
            connect.addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    logger.info("The client has connected [{}] successful!", inetSocketAddress.toString());
                    throw new IllegalStateException();
                }
            });
            return connect.sync().channel();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

}
