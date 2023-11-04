package com.tracejp.yozu.rpc.core.annotation;

import com.tracejp.yozu.rpc.core.spring.RpcScanner;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * <p> 是否启用 rpc <p/>
 *
 * @author traceJP
 * @since 2023/10/31 9:15
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Import(RpcScanner.class)
@Documented
public @interface EnableYozuRpc {

    /**
     * 是否启用
     */
    boolean enable() default true;

    /**
     * 扫描包路径
     */
    String[] basePackage() default {"com.tracejp"};

}
