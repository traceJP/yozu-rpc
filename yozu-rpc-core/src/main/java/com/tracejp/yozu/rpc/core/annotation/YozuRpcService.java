package com.tracejp.yozu.rpc.core.annotation;

import java.lang.annotation.*;

/**
 * <p> Rpc服务实现方注解  <p/>
 *
 * @author traceJP
 * @since 2023/10/31 9:13
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface YozuRpcService {

    /**
     * 服务版本
     */
    String version() default "";

    /**
     * 服务组
     */
    String group() default "";

}
