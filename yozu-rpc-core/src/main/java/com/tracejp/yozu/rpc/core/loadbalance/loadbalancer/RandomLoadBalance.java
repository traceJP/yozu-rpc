package com.tracejp.yozu.rpc.core.loadbalance.loadbalancer;

import com.tracejp.yozu.rpc.core.loadbalance.AbsLoadBalance;
import com.tracejp.yozu.rpc.core.transport.domain.RpcRequest;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

/**
 * <p> 随机负载均衡实现 <p/>
 *
 * @author traceJP
 * @since 2023/10/31 10:37
 */
public class RandomLoadBalance extends AbsLoadBalance {

    @Override
    protected InetSocketAddress doVote(List<InetSocketAddress> serviceAddresses, RpcRequest rpcRequest) {
        Random random = new Random();
        return serviceAddresses.get(random.nextInt(serviceAddresses.size()));
    }

}
