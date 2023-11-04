package com.tracejp.yozu.rpc.core.proxy;

import com.tracejp.yozu.rpc.core.enums.RpcErrorMessageEnum;
import com.tracejp.yozu.rpc.core.enums.RpcResponseCodeEnum;
import com.tracejp.yozu.rpc.core.exception.RpcException;
import com.tracejp.yozu.rpc.core.provider.domain.RpcServiceConfig;
import com.tracejp.yozu.rpc.core.transport.NettyRpcClient;
import com.tracejp.yozu.rpc.core.transport.domain.RpcRequest;
import com.tracejp.yozu.rpc.core.transport.domain.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p> 调用方注入代理对象 <p/>
 *
 * @author traceJP
 * @since 2023/11/2 16:21
 */
public class RpcClientProxy implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);

    private final NettyRpcClient rpcClient;

    private final RpcServiceConfig serviceConfig;

    private final Integer timeout;

    public RpcClientProxy(NettyRpcClient rpcClient, RpcServiceConfig serviceConfig, Integer timeout) {
        this.rpcClient = rpcClient;
        this.serviceConfig = serviceConfig;
        this.timeout = timeout;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.info("invoked method: [{}]", method.getName());

        // 构造请求
        RpcRequest request = new RpcRequest();
        request.setMethodName(method.getName());
        request.setInterfaceName(method.getDeclaringClass().getName());
        request.setParamTypes(method.getParameterTypes());
        request.setParameters(args);
        request.setRequestId(UUID.randomUUID().toString());
        request.setGroup(serviceConfig.getGroup());
        request.setVersion(serviceConfig.getVersion());

        RpcResponse<Object> response = rpcClient.sendRpcRequest(request)
                .sync().get(timeout, TimeUnit.MILLISECONDS);
        this.check(response, request);
        return response.getData();
    }

    /**
     * 检查 rpc 响应
     */
    private void check(RpcResponse<Object> rpcResponse, RpcRequest rpcRequest) {
        if (rpcResponse == null) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, "interfaceName :" + rpcRequest.getInterfaceName());
        }
        if (!Objects.equals(rpcRequest.getRequestId(), rpcResponse.getRequestId())) {
            throw new RpcException(RpcErrorMessageEnum.REQUEST_NOT_MATCH_RESPONSE, "interfaceName:" + rpcRequest.getInterfaceName());
        }
        if (rpcResponse.getCode() == null || Objects.equals(rpcResponse.getCode(), RpcResponseCodeEnum.FAIL.getCode())) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, "interfaceName:" + rpcRequest.getInterfaceName());
        }
    }

}
