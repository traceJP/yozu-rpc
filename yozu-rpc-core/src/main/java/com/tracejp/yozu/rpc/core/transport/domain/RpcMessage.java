package com.tracejp.yozu.rpc.core.transport.domain;

/**
 * <p> Rpc 消息基类 <p/>
 *
 * @author traceJP
 * @since 2023/10/31 9:20
 */
public class RpcMessage {

    /**
     * 消息类型
     */
    private byte messageType;

    /**
     * 序列化类型
     */
    private byte codec;

    /**
     * 压缩类型
     */
    private byte compress;

    /**
     * 请求id
     */
    private int requestId;

    /**
     * 请求数据
     */
    private Object data;

    public RpcMessage() {
    }

    public RpcMessage(byte messageType, byte codec, byte compress, int requestId, Object data) {
        this.messageType = messageType;
        this.codec = codec;
        this.compress = compress;
        this.requestId = requestId;
        this.data = data;
    }

    public byte getMessageType() {
        return messageType;
    }

    public void setMessageType(byte messageType) {
        this.messageType = messageType;
    }

    public byte getCodec() {
        return codec;
    }

    public void setCodec(byte codec) {
        this.codec = codec;
    }

    public byte getCompress() {
        return compress;
    }

    public void setCompress(byte compress) {
        this.compress = compress;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RpcMessage{" +
                "messageType=" + messageType +
                ", codec=" + codec +
                ", compress=" + compress +
                ", requestId=" + requestId +
                ", data=" + data +
                '}';
    }

}
