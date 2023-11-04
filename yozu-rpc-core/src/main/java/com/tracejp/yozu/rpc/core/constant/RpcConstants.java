package com.tracejp.yozu.rpc.core.constant;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * <p> Rpc 常量 <p/>
 *
 * @author traceJP
 * @since 2023/10/30 16:37
 */
public class RpcConstants {

    /**
     * 魔数 4byte
     */
    public static final byte[] MAGIC_NUMBER = {
            (byte) 'y',
            (byte) 'o',
            (byte) 'z',
            (byte) 'u'
    };

    /**
     * 默认编码
     */
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    /**
     * 版本信息
     */
    public static final byte VERSION = 1;

    /**
     * 消息头长度
     */
    public static final int HEAD_LENGTH = 16;

    /**
     * ping
     */
    public static final String PING = "ping";

    /**
     * pong
     */
    public static final String PONG = "pong";

    /**
     * 最大帧长度 8M
     */
    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;

}
