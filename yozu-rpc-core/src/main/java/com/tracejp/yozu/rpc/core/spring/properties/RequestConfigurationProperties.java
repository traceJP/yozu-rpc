package com.tracejp.yozu.rpc.core.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>  <p/>
 *
 * @author traceJP
 * @since 2023/11/3 9:45
 */
@ConfigurationProperties(prefix = "yozu.rpc.request")
public class RequestConfigurationProperties {

    /**
     * 心跳请求 序列化方式
     */
    private String hbSerialize = "protostuff";

    /**
     * 心跳请求 压缩方式
     */
    private String hbCompress = "gzip";

    /**
     * rpc 请求 序列化方式
     */
    private String rpcSerialize = "protostuff";

    /**
     * rpc 请求 压缩方式
     */
    private String rpcCompress = "gzip";

    /**
     * rpc 请求 超时时间 ms
     */
    private Integer rpcTimeout = 5000;

    public String getHbSerialize() {
        return hbSerialize;
    }

    public String getHbCompress() {
        return hbCompress;
    }

    public String getRpcSerialize() {
        return rpcSerialize;
    }

    public String getRpcCompress() {
        return rpcCompress;
    }

    public Integer getRpcTimeout() {
        return rpcTimeout;
    }

    public void setHbSerialize(String hbSerialize) {
        this.hbSerialize = hbSerialize;
    }

    public void setHbCompress(String hbCompress) {
        this.hbCompress = hbCompress;
    }

    public void setRpcSerialize(String rpcSerialize) {
        this.rpcSerialize = rpcSerialize;
    }

    public void setRpcCompress(String rpcCompress) {
        this.rpcCompress = rpcCompress;
    }

    public void setRpcTimeout(Integer rpcTimeout) {
        this.rpcTimeout = rpcTimeout;
    }

    @Override
    public String toString() {
        return "RequestConfigurationProperties{" +
                "hbSerialize='" + hbSerialize + '\'' +
                ", hbCompress='" + hbCompress + '\'' +
                ", rpcSerialize='" + rpcSerialize + '\'' +
                ", rpcCompress='" + rpcCompress + '\'' +
                '}';
    }

}
