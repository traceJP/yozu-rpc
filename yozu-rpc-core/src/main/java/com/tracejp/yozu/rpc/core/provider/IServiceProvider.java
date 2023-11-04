package com.tracejp.yozu.rpc.core.provider;

import com.tracejp.yozu.rpc.core.loadbalance.ILoadBalance;
import com.tracejp.yozu.rpc.core.spring.properties.ProviderConfigurationProperties;
import com.tracejp.yozu.rpc.core.transport.domain.RpcRequest;

import java.net.InetSocketAddress;

/**
 * <p> 服务注册发现接口 <p/>
 *
 * @author traceJP
 * @since 2023/10/31 9:47
 */
public interface IServiceProvider {

    /**
     * 初始化服务发现 - 依赖注入
     */
    void init(ProviderConfigurationProperties properties, ILoadBalance loadBalance);

    /**
     * 根据 Rpc请求 查找服务地址
     */
    InetSocketAddress lookupService(RpcRequest request);

    /**
     * 注册服务到指定的地址
     */
    void registerService(String serviceName, InetSocketAddress address);

}
