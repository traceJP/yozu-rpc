package com.tracejp.yozu.rpc.core.loadbalance;


import com.tracejp.yozu.rpc.core.transport.domain.RpcRequest;
import com.tracejp.yozu.rpc.core.utils.CollectionUtil;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * <p> 负载均衡 <p/>
 *
 * @author traceJP
 * @since 2023/10/31 10:33
 */
public abstract class AbsLoadBalance implements ILoadBalance {

    @Override
    public InetSocketAddress vote(List<InetSocketAddress> serviceAddresses, RpcRequest rpcRequest) {
        if (CollectionUtil.isEmpty(serviceAddresses)) {
            return null;
        }
        if (serviceAddresses.size() == 1) {
            return serviceAddresses.get(0);
        }
        return doVote(serviceAddresses, rpcRequest);
    }

    protected abstract InetSocketAddress doVote(List<InetSocketAddress> serviceAddresses, RpcRequest rpcRequest);

}
