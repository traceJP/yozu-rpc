package com.tracejp.yozu.rpc.core.enums;

/**
 * <p> 注册发现提供者 策略枚举 <p/>
 *
 * @author traceJP
 * @since 2023/11/1 9:40
 */
public enum ServerProviderTypeEnum {

    NACOS("nacos");

    private final String name;

    ServerProviderTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
