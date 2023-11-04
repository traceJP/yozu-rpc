package com.tracejp.example.server.remote;

import com.tracejp.exmaple.service.api.HelloService;
import com.tracejp.exmaple.service.api.domain.Hello;
import com.tracejp.yozu.rpc.core.annotation.YozuRpcService;

/**
 * <p> 服务提供者 <p/>
 *
 * @author traceJP
 * @since 2023/11/2 14:58
 */
@YozuRpcService
public class HelloServiceImpl implements HelloService {

    @Override
    public String echo(Hello hello) {
        return "Hello " + this.getClass().getName() + " : " + hello.getName();
    }

}
