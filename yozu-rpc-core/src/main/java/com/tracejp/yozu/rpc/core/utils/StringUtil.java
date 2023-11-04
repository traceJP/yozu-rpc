package com.tracejp.yozu.rpc.core.utils;

/**
 * String 工具类
 *
 * @author traceJP
 * @since 2023/10/31 11:15
 */
public class StringUtil {

    public static boolean isBlank(String s) {
        if (s == null || s.length() == 0) {
            return true;
        }
        for (int i = 0; i < s.length(); ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
