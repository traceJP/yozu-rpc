package com.tracejp.yozu.rpc.core.spring;


import com.tracejp.yozu.rpc.core.annotation.EnableYozuRpc;
import com.tracejp.yozu.rpc.core.annotation.YozuRpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;

/**
 * <p> Rpc接口扫描 <p/>
 *
 * @author traceJP
 * @since 2023/11/2 11:38
 */
public class RpcScanner implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private static final Logger logger = LoggerFactory.getLogger(RpcScanner.class);

    private static final String ENABLE_ATTRIBUTE_NAME = "enable";

    private static final String BASE_PACKAGE_ATTRIBUTE_NAME = "basePackage";

    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata,
                                        BeanDefinitionRegistry beanDefinitionRegistry) {
        AnnotationAttributes enableAnnotation = AnnotationAttributes
                .fromMap(annotationMetadata.getAnnotationAttributes(EnableYozuRpc.class.getName()));

        String[] rpcScanBasePackages = new String[0];
        if (enableAnnotation != null) {
            if (!enableAnnotation.getBoolean(ENABLE_ATTRIBUTE_NAME)) {
                return;
            }
            rpcScanBasePackages = enableAnnotation.getStringArray(BASE_PACKAGE_ATTRIBUTE_NAME);
        }
        if (rpcScanBasePackages.length == 0) {
            rpcScanBasePackages = new String[]{
                    ((StandardAnnotationMetadata) annotationMetadata).getIntrospectedClass().getPackage().getName()
            };
        }

        // 扫描 RpcService 注解
        ServiceScanner rpcServiceScanner = new ServiceScanner(beanDefinitionRegistry, YozuRpcService.class);

        if (resourceLoader != null) {
            rpcServiceScanner.setResourceLoader(resourceLoader);
        }

        int rpcServiceCount = rpcServiceScanner.scan(rpcScanBasePackages);
        logger.info("rpcService 扫描的数量 [{}]", rpcServiceCount);
    }

    private static class ServiceScanner extends ClassPathBeanDefinitionScanner {

        public ServiceScanner(BeanDefinitionRegistry registry, Class<? extends Annotation> annoType) {
            super(registry);
            super.addIncludeFilter(new AnnotationTypeFilter(annoType));
        }

        @Override
        public int scan(String... basePackages) {
            return super.scan(basePackages);
        }

    }

}
