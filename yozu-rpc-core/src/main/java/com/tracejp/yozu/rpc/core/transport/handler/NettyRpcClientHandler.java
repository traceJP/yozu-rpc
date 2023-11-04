package com.tracejp.yozu.rpc.core.transport.handler;

import com.tracejp.yozu.rpc.core.constant.RpcConstants;
import com.tracejp.yozu.rpc.core.enums.CompressTypeEnum;
import com.tracejp.yozu.rpc.core.enums.RpcMessageTypeEnum;
import com.tracejp.yozu.rpc.core.enums.SerializationTypeEnum;
import com.tracejp.yozu.rpc.core.spring.properties.RequestConfigurationProperties;
import com.tracejp.yozu.rpc.core.transport.NettyRpcClient;
import com.tracejp.yozu.rpc.core.transport.context.UnprocessedRequestContext;
import com.tracejp.yozu.rpc.core.transport.domain.RpcMessage;
import com.tracejp.yozu.rpc.core.transport.domain.RpcResponse;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * <p> Rpc 客户端消息处理器 <p/>
 *
 * @author traceJP
 * @since 2023/11/1 14:46
 */
@ChannelHandler.Sharable
public class NettyRpcClientHandler extends SimpleChannelInboundHandler<RpcMessage> {

    private static final Logger logger = LoggerFactory.getLogger(NettyRpcClientHandler.class);

    private final NettyRpcClient rpcClient;

    private final RequestConfigurationProperties requestConfigurationProperties;

    public NettyRpcClientHandler(NettyRpcClient rpcClient, RequestConfigurationProperties requestConfigurationProperties) {
        this.rpcClient = rpcClient;
        this.requestConfigurationProperties = requestConfigurationProperties;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage msg) {
        try {
            byte messageType = msg.getMessageType();

            // 心跳响应消息
            if (messageType == RpcMessageTypeEnum.HEARTBEAT_RESPONSE_TYPE.getType()) {
                logger.info("receive heartbeat response from server: [{}]", msg.getData());
            }

            // 普通响应消息
            if (messageType == RpcMessageTypeEnum.RESPONSE_TYPE.getType()) {
                RpcResponse<Object> data = (RpcResponse<Object>) msg.getData();
                UnprocessedRequestContext.complete(data);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                logger.info("write idle happen [{}]", ctx.channel().remoteAddress());
                Channel channel = rpcClient.getChannel((InetSocketAddress) ctx.channel().remoteAddress());
                RpcMessage message = new RpcMessage();
                SerializationTypeEnum serializationTypeEnum = SerializationTypeEnum
                        .nameOf(requestConfigurationProperties.getHbSerialize());
                CompressTypeEnum compressTypeEnum = CompressTypeEnum
                        .nameOf(requestConfigurationProperties.getHbCompress());
                message.setCodec(serializationTypeEnum.getCode());
                message.setCompress(compressTypeEnum.getCode());
                message.setMessageType(RpcMessageTypeEnum.HEARTBEAT_REQUEST_TYPE.getType());
                message.setData(RpcConstants.PING);
                channel.writeAndFlush(message).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("client catch exception：", cause);
        cause.printStackTrace();
        ctx.close();
    }

}
