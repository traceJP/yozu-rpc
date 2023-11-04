package com.tracejp.yozu.rpc.core.transport.context;

import com.tracejp.yozu.rpc.core.transport.domain.RpcResponse;
import io.netty.util.concurrent.Promise;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p> 未处理请求上下文 <p/>
 *
 * @author traceJP
 * @since 2023/11/1 14:58
 */
public class UnprocessedRequestContext {

    /**
     * 未响应的请求集合 promise
     */
    private static final Map<String, Promise<RpcResponse<Object>>> UNPROCESSED_RESPONSE_PROMISES;

    static {
        UNPROCESSED_RESPONSE_PROMISES = new ConcurrentHashMap<>();
    }

    /**
     * 添加未处理的请求
     */
    public static void put(String requestId, Promise<RpcResponse<Object>> promise) {
        UNPROCESSED_RESPONSE_PROMISES.put(requestId, promise);
    }

    /**
     * 处理响应
     */
    public static void complete(RpcResponse<Object> response) {
        Promise<RpcResponse<Object>> promise = UNPROCESSED_RESPONSE_PROMISES.remove(response.getRequestId());
        if (promise == null) {
            throw new IllegalStateException();
        }
        promise.setSuccess(response);
    }

}
