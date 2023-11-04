package com.tracejp.yozu.rpc.core.serialize;

/**
 * <p> 序列化接口 <p/>
 *
 * @author traceJP
 * @since 2023/10/31 14:10
 */
public interface ISerializer {

    /**
     * 序列化
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);

}
