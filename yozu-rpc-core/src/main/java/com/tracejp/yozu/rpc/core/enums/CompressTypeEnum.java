package com.tracejp.yozu.rpc.core.enums;

import java.util.Objects;

/**
 * <p> 压缩方式 策略枚举 <p/>
 *
 * @author traceJP
 * @since 2023/11/1 9:50
 */
public enum CompressTypeEnum {

    GZIP((byte) 0x01, "gzip");

    private final byte code;

    private final String name;

    CompressTypeEnum(byte code, String name) {
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
        for (CompressTypeEnum c : CompressTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }

    public static CompressTypeEnum nameOf(String name) {
        for (CompressTypeEnum n : CompressTypeEnum.values()) {
            if (Objects.equals(n.getName(), name)) {
                return n;
            }
        }
        throw new RuntimeException("未知的压缩类型: " + name);
    }

}
