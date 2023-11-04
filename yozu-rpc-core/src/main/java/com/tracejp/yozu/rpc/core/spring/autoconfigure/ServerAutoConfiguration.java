package com.tracejp.yozu.rpc.core.spring.autoconfigure;

import com.tracejp.yozu.rpc.core.provider.ServiceProviderManager;
import com.tracejp.yozu.rpc.core.spring.properties.YozuRpcConfigurationProperties;
import com.tracejp.yozu.rpc.core.transport.NettyRpcServer;
import com.tracejp.yozu.rpc.core.transport.codec.RpcMessageEncoder;
import com.tracejp.yozu.rpc.core.transport.hook.RpcShutdownHook;
import org.springframework.context.annotation.Bean;

/**
 * <p> 服务端自动配置 <p/>
 *
 * @author traceJP
 * @since 2023/11/2 11:15
 */
public class ServerAutoConfiguration {

    private final RpcMessageEncoder rpcMessageEncoder;

    private final RpcShutdownHook rpcShutdownHook;

    private final ServiceProviderManager serviceProviderManager;

    private final YozuRpcConfigurationProperties yozuRpcConfigurationProperties;

    public ServerAutoConfiguration(RpcMessageEncoder rpcMessageEncoder,
                                   RpcShutdownHook rpcShutdownHook,
                                   ServiceProviderManager serviceProviderManager,
                                   YozuRpcConfigurationProperties yozuRpcConfigurationProperties) {
        this.rpcMessageEncoder = rpcMessageEncoder;
        this.rpcShutdownHook = rpcShutdownHook;
        this.serviceProviderManager = serviceProviderManager;
        this.yozuRpcConfigurationProperties = yozuRpcConfigurationProperties;
    }

    @Bean
    public NettyRpcServer nettyRpcServer() {
        NettyRpcServer nettyRpcServer = new NettyRpcServer(rpcShutdownHook,
                rpcMessageEncoder,
                serviceProviderManager,
                yozuRpcConfigurationProperties.getInetSocketAddress());
        new Thread(nettyRpcServer).start();
        return nettyRpcServer;
    }

}
