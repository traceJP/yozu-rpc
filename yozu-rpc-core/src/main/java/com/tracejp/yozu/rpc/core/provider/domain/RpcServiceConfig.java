package com.tracejp.yozu.rpc.core.provider.domain;

/**
 * <p>  <p/>
 *
 * @author traceJP
 * @since 2023/11/2 10:49
 */
public class RpcServiceConfig {

    /**
     * 版本
     */
    private String version = "";

    /**
     * 分组
     */
    private String group = "";

    /**
     * 目标服务
     */
    private Object service;

    public String getRpcServiceName() {
        return this.getServiceName() + this.getGroup() + this.getVersion();
    }

    public String getServiceName() {
        return this.service.getClass().getInterfaces()[0].getCanonicalName();
    }

    public RpcServiceConfig() {
    }

    public RpcServiceConfig(String version, String group, Object service) {
        this.version = version;
        this.group = group;
        this.service = service;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Object getService() {
        return service;
    }

    public void setService(Object service) {
        this.service = service;
    }

    @Override
    public String toString() {
        return "RpcServiceConfig{" +
                "version='" + version + '\'' +
                ", group='" + group + '\'' +
                ", service=" + service +
                '}';
    }

}
