package com.tracejp.yozu.rpc.core.annotation;

import java.lang.annotation.*;

/**
 * <p> Rpc服务调用方注解 <p/>
 *
 * @author traceJP
 * @since 2023/11/2 16:12
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface YozuRpcAutowired {

    /**
     * 服务版本
     */
    String version() default "";

    /**
     * 服务组
     */
    String group() default "";

}
