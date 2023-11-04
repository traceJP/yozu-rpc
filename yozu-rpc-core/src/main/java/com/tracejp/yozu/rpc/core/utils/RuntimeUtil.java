package com.tracejp.yozu.rpc.core.utils;

/**
 * <p> 运行时工具 <p/>
 *
 * @author traceJP
 * @since 2023/10/31 11:15
 */
public class RuntimeUtil {

    /**
     * 获取CPU的核心数
     */
    public static int cpus() {
        return Runtime.getRuntime().availableProcessors();
    }

}
