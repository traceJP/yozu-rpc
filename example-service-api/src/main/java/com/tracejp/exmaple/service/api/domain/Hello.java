package com.tracejp.exmaple.service.api.domain;

/**
 * <p>  <p/>
 *
 * @author traceJP
 * @since 2023/10/30 16:20
 */
public class Hello {

    private String name;

    public Hello() {
    }

    public Hello(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Hello{" +
                "name='" + name + '\'' +
                '}';
    }

}
