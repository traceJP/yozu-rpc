package com.tracejp.yozu.rpc.core.provider;

import com.tracejp.yozu.rpc.core.enums.RpcErrorMessageEnum;
import com.tracejp.yozu.rpc.core.exception.RpcException;
import com.tracejp.yozu.rpc.core.provider.domain.RpcServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p> 服务代理管理器 <p/>
 *
 * @author traceJP
 * @since 2023/11/2 10:46
 */
public interface ServiceProviderManager {

    /**
     * 获取服务
     */
    Object getService(String rpcServiceName);

    /**
     * 添加服务
     */
    void putService(RpcServiceConfig rpcServiceConfig);

    /**
     * 服务代理管理器实现
     */
    class ServiceProviderManagerImpl implements ServiceProviderManager {

        private static final Logger logger = LoggerFactory.getLogger(ServiceProviderManagerImpl.class);

        private final Map<String, Object> serviceMap;

        private final Set<String> registeredService;

        private final IServiceProvider serviceRegistry;

        private final InetSocketAddress address;

        public ServiceProviderManagerImpl(IServiceProvider serviceProvider, InetSocketAddress address) {
            this.serviceRegistry = serviceProvider;
            this.address = address;
            serviceMap = new ConcurrentHashMap<>();
            registeredService = ConcurrentHashMap.newKeySet();
        }

        @Override
        public Object getService(String rpcServiceName) {
            Object service = serviceMap.get(rpcServiceName);
            if (service == null) {
                throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
            }
            return service;
        }

        @Override
        public void putService(RpcServiceConfig rpcServiceConfig) {
            String rpcServiceName = rpcServiceConfig.getRpcServiceName();
            if (!registeredService.contains(rpcServiceName)) {
                registeredService.add(rpcServiceName);
                serviceMap.put(rpcServiceName, rpcServiceConfig.getService());
                logger.info("Add service: {} and interfaces:{}", rpcServiceName,
                        rpcServiceConfig.getService().getClass().getInterfaces());
            }
            serviceRegistry.registerService(rpcServiceConfig.getRpcServiceName(), address);
        }

    }

}
