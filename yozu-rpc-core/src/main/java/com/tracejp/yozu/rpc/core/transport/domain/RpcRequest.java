package com.tracejp.yozu.rpc.core.transport.domain;

import java.util.Arrays;

/**
 * <p>  <p/>
 *
 * @author traceJP
 * @since 2023/10/31 9:23
 */
public class RpcRequest {

    private static final long serialVersionUID = -1L;

    /**
     * 请求id
     */
    private String requestId;

    /**
     * 接口名
     */
    private String interfaceName;

    /**
     * 接口方法名
     */
    private String methodName;

    /**
     * 参数值列表
     */
    private Object[] parameters;

    /**
     * 参数值列表类型
     */
    private Class<?>[] paramTypes;

    /**
     * 版本号
     */
    private String version;

    /**
     * 组名
     */
    private String group;

    /**
     * 获取服务名
     */
    public String getRpcServiceName() {
        return this.getInterfaceName() + this.getGroup() + this.getVersion();
    }

    public RpcRequest() {
    }

    public RpcRequest(String requestId, String interfaceName, String methodName, Object[] parameters, Class<?>[] paramTypes, String version, String group) {
        this.requestId = requestId;
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.parameters = parameters;
        this.paramTypes = paramTypes;
        this.version = version;
        this.group = group;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
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

    @Override
    public String toString() {
        return "RpcRequest{" +
                "requestId='" + requestId + '\'' +
                ", interfaceName='" + interfaceName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", parameters=" + Arrays.toString(parameters) +
                ", paramTypes=" + Arrays.toString(paramTypes) +
                ", version='" + version + '\'' +
                ", group='" + group + '\'' +
                '}';
    }

}
