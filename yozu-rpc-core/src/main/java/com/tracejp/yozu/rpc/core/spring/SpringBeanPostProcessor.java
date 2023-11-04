package com.tracejp.yozu.rpc.core.spring;

import com.tracejp.yozu.rpc.core.annotation.YozuRpcAutowired;
import com.tracejp.yozu.rpc.core.annotation.YozuRpcService;
import com.tracejp.yozu.rpc.core.provider.ServiceProviderManager;
import com.tracejp.yozu.rpc.core.provider.domain.RpcServiceConfig;
import com.tracejp.yozu.rpc.core.proxy.RpcClientProxy;
import com.tracejp.yozu.rpc.core.spring.properties.RequestConfigurationProperties;
import com.tracejp.yozu.rpc.core.transport.NettyRpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * <p> Bean加载后置处理器 <p/>
 *
 * @author traceJP
 * @since 2023/11/2 11:38
 */
public class SpringBeanPostProcessor implements BeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(SpringBeanPostProcessor.class);

    private final ServiceProviderManager serviceProviderManager;

    private final NettyRpcClient rpcClient;

    private final RequestConfigurationProperties requestConfigurationProperties;

    public SpringBeanPostProcessor(ServiceProviderManager serviceProviderManager,
                                   NettyRpcClient rpcClient,
                                   RequestConfigurationProperties requestConfigurationProperties) {
        this.serviceProviderManager = serviceProviderManager;
        this.rpcClient = rpcClient;
        this.requestConfigurationProperties = requestConfigurationProperties;
    }

    /**
     * 提供方服务发布
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(YozuRpcService.class)) {
            logger.info("[{}] is annotated with  [{}]", bean.getClass().getName(), YozuRpcService.class.getCanonicalName());
            YozuRpcService rpcService = bean.getClass().getAnnotation(YozuRpcService.class);
            RpcServiceConfig config = new RpcServiceConfig();
            config.setVersion(rpcService.version());
            config.setGroup(rpcService.group());
            config.setService(bean);
            serviceProviderManager.putService(config);
        }
        return bean;
    }

    /**
     * 调用方代理注入
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        Field[] declaredFields = targetClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            YozuRpcAutowired annotation = declaredField.getAnnotation(YozuRpcAutowired.class);
            if (annotation != null) {
                RpcServiceConfig config = new RpcServiceConfig();
                config.setGroup(annotation.group());
                config.setVersion(annotation.version());

                // 代理
                RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient, config, requestConfigurationProperties.getRpcTimeout());
                Object clientProxy = rpcClientProxy.getProxy(declaredField.getType());

                declaredField.setAccessible(true);
                try {
                    declaredField.set(bean, clientProxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }

}
