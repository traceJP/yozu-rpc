package com.tracejp.yozu.rpc.core.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>  <p/>
 *
 * @author traceJP
 * @since 2023/10/31 17:13
 */
@ConfigurationProperties(prefix = "yozu.rpc.provider")
public class ProviderConfigurationProperties {

    /**
     * 服务发现类型
     */
    private String discoveryType;

    /**
     * 服务发现地址
     */
    private String serverAddress;

    /**
     * 负载均衡算法
     */
    private String loadBalanceType;

    /**
     * 连接最大重试次数 - 默认 5 次
     */
    private Integer connectMaxRetry = 5;

    /**
     * 连接重试间隔 - 默认 5000 ms
     */
    private Integer connectRetryInterval = 5000;

    public String getDiscoveryType() {
        return discoveryType;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public String getLoadBalanceType() {
        return loadBalanceType;
    }

    public Integer getConnectMaxRetry() {
        return connectMaxRetry;
    }

    public Integer getConnectRetryInterval() {
        return connectRetryInterval;
    }

    public void setDiscoveryType(String discoveryType) {
        this.discoveryType = discoveryType;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public void setLoadBalanceType(String loadBalanceType) {
        this.loadBalanceType = loadBalanceType;
    }

    public void setConnectMaxRetry(Integer connectMaxRetry) {
        this.connectMaxRetry = connectMaxRetry;
    }

    public void setConnectRetryInterval(Integer connectRetryInterval) {
        this.connectRetryInterval = connectRetryInterval;
    }

    @Override
    public String toString() {
        return "ProviderConfigurationProperties{" +
                "discoveryType='" + discoveryType + '\'' +
                ", serverAddress='" + serverAddress + '\'' +
                ", loadBalanceType='" + loadBalanceType + '\'' +
                ", connectMaxRetry=" + connectMaxRetry +
                ", connectRetryInterval=" + connectRetryInterval +
                '}';
    }

}
