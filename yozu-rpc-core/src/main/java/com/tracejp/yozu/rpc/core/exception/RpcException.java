package com.tracejp.yozu.rpc.core.exception;

import com.tracejp.yozu.rpc.core.enums.RpcErrorMessageEnum;

/**
 * <p> RPC异常 <p/>
 *
 * @author traceJP
 * @since 2023/10/31 10:28
 */
public class RpcException extends RuntimeException {

    public RpcException(RpcErrorMessageEnum rpcErrorMessageEnum, String detail) {
        super(rpcErrorMessageEnum.getMessage() + ":" + detail);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(RpcErrorMessageEnum rpcErrorMessageEnum) {
        super(rpcErrorMessageEnum.getMessage());
    }

}
