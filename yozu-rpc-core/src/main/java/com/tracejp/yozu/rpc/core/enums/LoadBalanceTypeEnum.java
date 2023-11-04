package com.tracejp.yozu.rpc.core.enums;

/**
 * <p> 负载均衡类型 策略枚举 <p/>
 *
 * @author traceJP
 * @since 2023/11/1 9:41
 */
public enum LoadBalanceTypeEnum {

    /**
     * 随机
     */
    RANDOM("random"),

    /**
     * 轮询
     */
    ROUND_ROBIN("roundRobin"),

    /**
     * 一致性哈希
     */
    CONSISTENT_HASH("consistentHash");

    private final String name;

    LoadBalanceTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
