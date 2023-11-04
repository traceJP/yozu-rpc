package com.tracejp.yozu.rpc.core.transport.context;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p> Channel集合上下文 <p/>
 *
 * @author traceJP
 * @since 2023/11/1 15:14
 */
public class ChannelProviderContext {

    private static final Logger logger = LoggerFactory.getLogger(ChannelProviderContext.class);

    /**
     * Channel集合
     */
    private static final Map<String, Channel> CHANNEL_MAP;

    static {
        CHANNEL_MAP = new ConcurrentHashMap<>();
    }

    public static Channel get(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        if (CHANNEL_MAP.containsKey(key)) {
            Channel channel = CHANNEL_MAP.get(key);
            if (channel != null && channel.isActive()) {
                return channel;
            }
            CHANNEL_MAP.remove(key);
        }
        return null;
    }

    public static void put(InetSocketAddress inetSocketAddress, Channel channel) {
        String key = inetSocketAddress.toString();
        CHANNEL_MAP.put(key, channel);
    }

    public static void remove(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        CHANNEL_MAP.remove(key);
        logger.info("Channel map size :[{}]", CHANNEL_MAP.size());
    }

}
