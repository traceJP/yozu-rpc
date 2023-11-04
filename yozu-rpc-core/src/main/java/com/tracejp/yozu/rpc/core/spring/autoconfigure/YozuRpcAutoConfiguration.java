package com.tracejp.yozu.rpc.core.spring.autoconfigure;

import com.tracejp.yozu.rpc.core.annotation.EnableYozuRpc;
import com.tracejp.yozu.rpc.core.provider.ServiceProviderManager;
import com.tracejp.yozu.rpc.core.spring.SpringBeanPostProcessor;
import com.tracejp.yozu.rpc.core.spring.properties.ProviderConfigurationProperties;
import com.tracejp.yozu.rpc.core.spring.properties.RequestConfigurationProperties;
import com.tracejp.yozu.rpc.core.spring.properties.YozuRpcConfigurationProperties;
import com.tracejp.yozu.rpc.core.transport.NettyRpcClient;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * <p>  <p/>
 *
 * @author traceJP
 * @since 2023/10/31 9:41
 */
@EnableConfigurationProperties({
        YozuRpcConfigurationProperties.class,
        ProviderConfigurationProperties.class,
        RequestConfigurationProperties.class
})
@ImportAutoConfiguration({
        ClientAutoConfiguration.class,
        ServerAutoConfiguration.class,
        NettyAutoConfiguration.class
})
@ConditionalOnBean(annotation = EnableYozuRpc.class)
public class YozuRpcAutoConfiguration {

    /**
     * bean 后置处理器
     */
    @Bean
    public SpringBeanPostProcessor springBeanPostProcessor(ServiceProviderManager serviceProviderManager,
                                                           NettyRpcClient rpcClient,
                                                           RequestConfigurationProperties requestConfigurationProperties) {
        return new SpringBeanPostProcessor(serviceProviderManager, rpcClient, requestConfigurationProperties);
    }

}
