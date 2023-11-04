package com.tracejp.example.server;

import com.tracejp.yozu.rpc.core.annotation.EnableYozuRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableYozuRpc(basePackage = "com.tracejp.example")
@SpringBootApplication
public class ExampleServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleServerApplication.class, args);
    }

}
