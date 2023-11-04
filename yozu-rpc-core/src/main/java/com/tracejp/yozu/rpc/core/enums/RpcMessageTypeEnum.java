package com.tracejp.yozu.rpc.core.enums;

/**
 * <p> 消息类型枚举 <p/>
 *
 * @author traceJP
 * @since 2023/10/31 14:27
 */
public enum RpcMessageTypeEnum {

    /**
     * 请求
     */
    REQUEST_TYPE((byte) 0x01),

    /**
     * 响应
     */
    RESPONSE_TYPE((byte) 0x02),

    /**
     * ping
     */
    HEARTBEAT_REQUEST_TYPE((byte) 0x03),

    /**
     * pong
     */
    HEARTBEAT_RESPONSE_TYPE((byte) 0x04);

    private final byte type;

    RpcMessageTypeEnum(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }

    public static boolean isHeartBeatType(byte type) {
        return HEARTBEAT_REQUEST_TYPE.type == type || HEARTBEAT_RESPONSE_TYPE.type == type;
    }

}
