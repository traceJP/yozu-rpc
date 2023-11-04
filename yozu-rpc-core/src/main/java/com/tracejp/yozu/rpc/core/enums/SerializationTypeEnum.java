package com.tracejp.yozu.rpc.core.enums;

import com.tracejp.yozu.rpc.core.exception.RpcException;

import java.util.Objects;

/**
 * <p> 序列化方式 策略枚举 <p/>
 *
 * @author traceJP
 * @since 2023/11/1 9:38
 */
public enum SerializationTypeEnum {

    /**
     * protostuff
     */
    PROTOSTUFF((byte) 0x01, "protostuff");

    private final byte code;

    private final String name;

    SerializationTypeEnum(byte code, String name) {
        this.code = code;
        this.name = name;
    }

    public byte getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static String getName(byte code) {
        for (SerializationTypeEnum c : SerializationTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }

    public static SerializationTypeEnum nameOf(String name) {
        for (SerializationTypeEnum n : SerializationTypeEnum.values()) {
            if (Objects.equals(n.getName(), name)) {
                return n;
            }
        }
        throw new RuntimeException("未知的序列化类型: " + name);
    }

}
