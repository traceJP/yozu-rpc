package com.tracejp.yozu.rpc.core.spring.autoconfigure;

import com.tracejp.yozu.rpc.core.provider.IServiceProvider;
import com.tracejp.yozu.rpc.core.spring.properties.RequestConfigurationProperties;
import com.tracejp.yozu.rpc.core.transport.NettyRpcClient;
import com.tracejp.yozu.rpc.core.transport.codec.RpcMessageEncoder;
import org.springframework.context.annotation.Bean;

/**
 * <p> 客户端自动配置 <p/>
 *
 * @author traceJP
 * @since 2023/11/2 16:50
 */
public class ClientAutoConfiguration {

    private final RpcMessageEncoder rpcMessageEncoder;

    private final IServiceProvider serviceProvider;

    private final RequestConfigurationProperties requestConfigurationProperties;

    public ClientAutoConfiguration(RpcMessageEncoder rpcMessageEncoder,
                                   IServiceProvider serviceProvider,
                                   RequestConfigurationProperties requestConfigurationProperties) {
        this.rpcMessageEncoder = rpcMessageEncoder;
        this.serviceProvider = serviceProvider;
        this.requestConfigurationProperties = requestConfigurationProperties;
    }

    @Bean
    public NettyRpcClient nettyRpcClient() {
        NettyRpcClient client = new NettyRpcClient(serviceProvider, rpcMessageEncoder, requestConfigurationProperties);
        new Thread(client).start();
        return client;
    }

}
