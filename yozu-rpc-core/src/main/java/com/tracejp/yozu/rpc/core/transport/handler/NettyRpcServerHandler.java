package com.tracejp.yozu.rpc.core.transport.handler;

import com.tracejp.yozu.rpc.core.constant.RpcConstants;
import com.tracejp.yozu.rpc.core.enums.RpcMessageTypeEnum;
import com.tracejp.yozu.rpc.core.enums.RpcResponseCodeEnum;
import com.tracejp.yozu.rpc.core.exception.RpcException;
import com.tracejp.yozu.rpc.core.provider.ServiceProviderManager;
import com.tracejp.yozu.rpc.core.transport.domain.RpcMessage;
import com.tracejp.yozu.rpc.core.transport.domain.RpcRequest;
import com.tracejp.yozu.rpc.core.transport.domain.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <p> Rpc 服务端消息处理器 <p/>
 *
 * @author traceJP
 * @since 2023/11/1 14:14
 */
@ChannelHandler.Sharable
public class NettyRpcServerHandler extends SimpleChannelInboundHandler<RpcMessage> {

    private static final Logger logger = LoggerFactory.getLogger(NettyRpcServerHandler.class);

    private final ServiceProviderManager serviceProviderManager;

    public NettyRpcServerHandler(ServiceProviderManager serviceProviderManager) {
        this.serviceProviderManager = serviceProviderManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage msg) {
        try {
            byte messageType = msg.getMessageType();

            // 心跳请求消息
            if (messageType == RpcMessageTypeEnum.HEARTBEAT_REQUEST_TYPE.getType()) {
                msg.setMessageType(RpcMessageTypeEnum.HEARTBEAT_RESPONSE_TYPE.getType());
                msg.setData(RpcConstants.PONG);
            }

            // 普通请求消息
            if (messageType == RpcMessageTypeEnum.REQUEST_TYPE.getType()) {
                msg.setMessageType(RpcMessageTypeEnum.RESPONSE_TYPE.getType());
                RpcRequest request = (RpcRequest) msg.getData();

                // 执行请求
                Object result = execute(request);

                RpcResponse<Object> response;
                if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                    response = RpcResponse.success(result, request.getRequestId());
                } else {
                    response = RpcResponse.fail(RpcResponseCodeEnum.FAIL);
                    logger.error("not writable now, message dropped");
                }
                msg.setData(response);
            }

            // 按客户端请求参数默认写回
            ctx.writeAndFlush(msg).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                logger.info("idle check happen, so close the connection");
                ctx.close();
            } else {
                super.userEventTriggered(ctx, evt);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("server catch exception");
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 执行请求
     */
    private Object execute(RpcRequest request) {
        Object service = serviceProviderManager.getService(request.getRpcServiceName());
        try {
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParamTypes());
            Object result = method.invoke(service, request.getParameters());
            logger.info("service:[{}] successful invoke method:[{}]", request.getInterfaceName(), request.getMethodName());
            return result;
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException |
                 IllegalAccessException e) {
            throw new RpcException(e.getMessage(), e);
        }
    }

}
