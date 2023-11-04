package com.tracejp.yozu.rpc.core.provider.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.tracejp.yozu.rpc.core.enums.RpcErrorMessageEnum;
import com.tracejp.yozu.rpc.core.exception.RpcException;
import com.tracejp.yozu.rpc.core.loadbalance.ILoadBalance;
import com.tracejp.yozu.rpc.core.provider.IServiceProvider;
import com.tracejp.yozu.rpc.core.spring.properties.ProviderConfigurationProperties;
import com.tracejp.yozu.rpc.core.transport.domain.RpcRequest;
import com.tracejp.yozu.rpc.core.utils.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * <p> Nacos 服务发现 <p/>
 *
 * @author traceJP
 * @since 2023/10/31 9:40
 */
public class NacosServiceProvider implements IServiceProvider {

    private static final Logger logger = LoggerFactory.getLogger(NacosServiceProvider.class);

    /**
     * 服务地址缓存
     */
    private static final Map<String, List<InetSocketAddress>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();

    /**
     * 已注册的服务地址
     */
    private static final Set<String> REGISTERED_PATH_SET = ConcurrentHashMap.newKeySet();

    private int connectMaxRetry;

    private int connectRetryInterval;

    protected NamingService namingService;

    protected ILoadBalance loadBalance;

    @Override
    public void init(ProviderConfigurationProperties properties, ILoadBalance loadBalance) {
        this.connectMaxRetry = properties.getConnectMaxRetry();
        this.connectRetryInterval = properties.getConnectRetryInterval();
        try {
            this.namingService = NamingFactory.createNamingService(properties.getServerAddress());
        } catch (NacosException e) {
            logger.error("NacosServiceClient init error", e);
            throw new RpcException(RpcErrorMessageEnum.PROVIDER_INIT_ERROR.getMessage(), e);
        }
        this.loadBalance = loadBalance;
    }

    @Override
    public void registerService(String serviceName, InetSocketAddress address) {
        int retry = 0;
        do {
            try {
                if (!REGISTERED_PATH_SET.contains(serviceName)) {
                    namingService.registerInstance(serviceName, address.getHostName(), address.getPort());
                }
                REGISTERED_PATH_SET.add(serviceName);
            } catch (NacosException e) {
                logger.error("NacosServiceClient registerService error: {}, retry times: {}", e, retry);
                try {
                    Thread.sleep(connectRetryInterval);
                } catch (InterruptedException ignored) {
                }
            }
        } while (retry++ < connectMaxRetry);
    }

    @Override
    public InetSocketAddress lookupService(RpcRequest request) {
        String serviceName = request.getRpcServiceName();
        try {
            List<InetSocketAddress> voteList;
            if (SERVICE_ADDRESS_MAP.containsKey(serviceName)) {  // 缓存
                voteList = SERVICE_ADDRESS_MAP.get(serviceName);
            } else {
                List<Instance> allInstances = namingService.getAllInstances(serviceName);
                if (CollectionUtil.isEmpty(allInstances)) {
                    logger.error("NacosServiceClient lookupService error, service not found, serviceName:{}", serviceName);
                    throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, serviceName);
                }
                voteList = allInstances.stream()
                        .map(instance -> new InetSocketAddress(instance.getIp(), instance.getPort()))
                        .collect(Collectors.toList());
                SERVICE_ADDRESS_MAP.put(serviceName, voteList);
                registerWatcher(serviceName);
            }
            return loadBalance.vote(voteList, request);
        } catch (NacosException e) {
            logger.error("NacosServiceClient lookupService error", e);
        }
        return null;
    }

    /**
     * 远程注册服务同步
     */
    private void registerWatcher(String serviceName) throws NacosException {
        namingService.subscribe(serviceName, event -> {
            if (event instanceof NamingEvent) {
                NamingEvent namingEvent = (NamingEvent) event;
                List<Instance> allInstances = namingEvent.getInstances();
                List<InetSocketAddress> voteList = allInstances.stream()
                        .map(instance -> new InetSocketAddress(instance.getIp(), instance.getPort()))
                        .collect(Collectors.toList());
                SERVICE_ADDRESS_MAP.put(serviceName, voteList);
            }
        });
    }

}
