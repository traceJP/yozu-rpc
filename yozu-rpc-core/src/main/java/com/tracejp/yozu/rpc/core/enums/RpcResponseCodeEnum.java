package com.tracejp.yozu.rpc.core.enums;

/**
 * <p>  <p/>
 *
 * @author traceJP
 * @since 2023/10/31 9:29
 */
public enum RpcResponseCodeEnum {

    /**
     * 成功
     */
    SUCCESS(200, "The remote call is successful"),

    /**
     * 失败
     */
    FAIL(500, "The remote call is fail");

    /**
     * 响应码
     */
    private final int code;

    /**
     * 响应消息
     */
    private final String message;

    RpcResponseCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "RpcResponseCodeEnum{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }

}
