package com.tracejp.yozu.rpc.core.spring.autoconfigure;

import com.tracejp.yozu.rpc.core.extension.ExtensionLoader;
import com.tracejp.yozu.rpc.core.loadbalance.ILoadBalance;
import com.tracejp.yozu.rpc.core.provider.IServiceProvider;
import com.tracejp.yozu.rpc.core.provider.ServiceProviderManager;
import com.tracejp.yozu.rpc.core.spring.properties.ProviderConfigurationProperties;
import com.tracejp.yozu.rpc.core.spring.properties.YozuRpcConfigurationProperties;
import com.tracejp.yozu.rpc.core.transport.codec.RpcMessageEncoder;
import com.tracejp.yozu.rpc.core.transport.hook.RpcShutdownHook;
import org.springframework.context.annotation.Bean;

/**
 * <p> Rpc Netty 自动配置 <p/>
 *
 * @author traceJP
 * @since 2023/10/31 16:16
 */
public class NettyAutoConfiguration {

    private final YozuRpcConfigurationProperties yozuRpcConfigurationProperties;

    public NettyAutoConfiguration(YozuRpcConfigurationProperties yozuRpcConfigurationProperties) {
        this.yozuRpcConfigurationProperties = yozuRpcConfigurationProperties;
    }

    /**
     * 编码器
     */
    @Bean
    public RpcMessageEncoder rpcMessageCodec() {
        return new RpcMessageEncoder();
    }

    /**
     * 服务优雅关闭钩子
     */
    @Bean
    public RpcShutdownHook rpcShutdownHook() {
        return new RpcShutdownHook();
    }

    /**
     * 服务提供者管理器
     */
    @Bean
    public ServiceProviderManager serviceProviderManager(IServiceProvider serviceProvider) {
        return new ServiceProviderManager.ServiceProviderManagerImpl(serviceProvider,
                yozuRpcConfigurationProperties.getInetSocketAddress());
    }

    /**
     * 服务发现注册
     */
    @Bean
    public IServiceProvider serviceProvider(ProviderConfigurationProperties properties) {
        String loadBalanceType = properties.getLoadBalanceType();
        ILoadBalance loadBalance = ExtensionLoader.getExtensionLoader(ILoadBalance.class)
                .getExtension(loadBalanceType);

        String discoveryType = properties.getDiscoveryType();
        IServiceProvider provider = ExtensionLoader.getExtensionLoader(IServiceProvider.class)
                .getExtension(discoveryType);

        provider.init(properties, loadBalance);
        return provider;
    }

}
