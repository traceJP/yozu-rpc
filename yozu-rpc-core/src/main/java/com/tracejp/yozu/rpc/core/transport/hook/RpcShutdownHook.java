package com.tracejp.yozu.rpc.core.transport.hook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>  <p/>
 *
 * @author traceJP
 * @since 2023/10/31 11:04
 */
public class RpcShutdownHook {

    private static final Logger logger = LoggerFactory.getLogger(RpcShutdownHook.class);

    public void clearAll() {
        logger.info("RpcShutdownHook clearAll");
    }

}
