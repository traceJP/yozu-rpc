package com.tracejp.yozu.rpc.core.compress;

/**
 * <p> 压缩接口 <p/>
 *
 * @author traceJP
 * @since 2023/11/1 11:02
 */
public interface ICompress {

    /**
     * 压缩
     */
    byte[] compress(byte[] bytes);


    /**
     * 解压
     */
    byte[] decompress(byte[] bytes);

}
