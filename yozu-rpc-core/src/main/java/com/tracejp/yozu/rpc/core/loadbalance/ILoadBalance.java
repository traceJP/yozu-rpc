package com.tracejp.yozu.rpc.core.loadbalance;

import com.tracejp.yozu.rpc.core.transport.domain.RpcRequest;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * <p> 负载均衡接口 <p/>
 *
 * @author traceJP
 * @since 2023/10/31 10:33
 */
public interface ILoadBalance {

    /**
     * 请求查找负载均衡
     */
    InetSocketAddress vote(List<InetSocketAddress> serviceUrlList, RpcRequest rpcRequest);

}
