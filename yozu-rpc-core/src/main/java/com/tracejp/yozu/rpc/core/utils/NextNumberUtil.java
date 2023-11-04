package com.tracejp.yozu.rpc.core.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p> 自增数工具 <p/>
 *
 * @author traceJP
 * @since 2023/10/31 14:10
 */
public class NextNumberUtil {

    /**
     * 用于存储下一个序列号的 Map
     * - key, 序列号
     */
    private final static Map<String, AtomicInteger> nextNumberMap;

    static {
        nextNumberMap = new ConcurrentHashMap<>();
    }

    public static int getIncr(String key) {
        AtomicInteger nextNumber = nextNumberMap.get(key);
        if (nextNumber == null) {
            nextNumber = new AtomicInteger(0);
            nextNumberMap.put(key, nextNumber);
        }
        return nextNumber.getAndIncrement();
    }

}
