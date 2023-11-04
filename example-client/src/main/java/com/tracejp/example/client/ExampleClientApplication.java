package com.tracejp.example.client;

import com.tracejp.yozu.rpc.core.annotation.EnableYozuRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableYozuRpc(basePackage = "com.tracejp.example")
@SpringBootApplication
public class ExampleClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleClientApplication.class, args);
    }

}
