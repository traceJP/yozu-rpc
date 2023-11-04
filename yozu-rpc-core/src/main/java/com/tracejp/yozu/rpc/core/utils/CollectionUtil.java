package com.tracejp.yozu.rpc.core.utils;

import org.springframework.lang.Nullable;

import java.util.Collection;

/**
 * <p>  <p/>
 *
 * @author traceJP
 * @since 2023/11/1 14:07
 */
public class CollectionUtil {

    public static boolean isEmpty(@Nullable Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

}
