package com.tracejp.yozu.rpc.core.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * <p> Rpc 常规配置 <p/>
 *
 * @author traceJP
 * @since 2023/10/31 16:12
 */
@ConfigurationProperties(prefix = "yozu.rpc")
public class YozuRpcConfigurationProperties {

    /**
     * RPC 注册主机
     */
    private String host;

    {
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * RPC 注册端口
     */
    private Integer port = 23333;

    public InetSocketAddress getInetSocketAddress() {
        return new InetSocketAddress(host, port);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "YozuRpcConfigurationProperties{" +
                "host='" + host + '\'' +
                ", port='" + port + '\'' +
                '}';
    }

}
