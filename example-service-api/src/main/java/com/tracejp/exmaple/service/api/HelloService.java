package com.tracejp.exmaple.service.api;

import com.tracejp.exmaple.service.api.domain.Hello;

/**
 * <p> 远程服务接口 <p/>
 *
 * @author traceJP
 * @since 2023/10/30 16:19
 */
public interface HelloService {

    String echo(Hello hello);

}
